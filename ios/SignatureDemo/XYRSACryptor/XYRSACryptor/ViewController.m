//
//  ViewController.m
//  XYRSACryptor
//
//  Created by Panda on 15/11/8.
//  Copyright © 2015年 Panda. All rights reserved.
//

#import "ViewController.h"
#import "XYSHASign.h"
#import "GTMBase64.h"
#import "XYRSACryptor.h"
#import "Define.h"
#import "HttpBase.h"

@interface ViewController ()

@property (nonatomic, strong) XYRSACryptor *rsaCryptor;
@property (nonatomic, strong) XYSHASign *sign;

@property (weak, nonatomic) IBOutlet UITextField *nameTF;
@property (weak, nonatomic) IBOutlet UITextField *nickTF;
@property (weak, nonatomic) IBOutlet UITextField *despTF;
- (IBAction)submit:(id)sender;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
  
}


- (XYRSACryptor *)rsaCryptor {
    if (!_rsaCryptor) {
        self.rsaCryptor = [[XYRSACryptor alloc] init];
    }
    return _rsaCryptor;
}

- (XYSHASign *)sign {
    if (!_sign) {
        self.sign = [[XYSHASign alloc] init];
        self.sign.passwordBlock = ^(){
            return @"123456";
        };
    }
    return _sign;
}


-(void)signatureAndPost{
//    var content = "desp=" + desp + "&nickname=" + nickname +"&timestamp="+ timestamp + "&username=" + uname;
    NSString *username = [_nameTF text];
    NSString *nickname = [_nickTF text];
    NSString *desp = [_despTF text];
    NSString *timestamp = [self getTime];

    NSString *content = [@"desp=" stringByAppendingString:desp];
    content = [content stringByAppendingString:@"&nickname="];
    content = [content stringByAppendingString:nickname];
    
    content = [content stringByAppendingString:@"&timestamp="];
    content = [content stringByAppendingString:timestamp];

    content = [content stringByAppendingString:@"&username="];
    content = [content stringByAppendingString:username];
    
    NSLog(@"待签名字符串：%@", content);
    
    NSString *sign = [self doSign:content];
    
    NSDictionary *params = @{ @"username":username,  @"nickname":nickname
                              ,@"desp":desp ,@"timestamp":timestamp
                              ,@"sign":sign };
    
    [HttpBase POST:[kBaseUrl stringByAppendingString:@"/rsa/signatureTest"] params:params success:^(id data, NSURLResponse *response) {
        
        NSLog(@"from backend: %@",data);
        
        UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"解密的密码为" message:data[@"data"] preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {}];
        
        [alert addAction:defaultAction];
        
        [self presentViewController:alert animated:YES completion:nil];
        
    } fail:^(NSError *error) {
        NSLog(@"%@",[error  localizedFailureReason]);
    }];

}

/**
 * 签名
 */
- (NSString*)doSign:(NSString*)content {
    
    NSString *p12Path = [[NSBundle mainBundle] pathForResource:@"private_key" ofType:@"p12"];
    [self.rsaCryptor loadPrivateKeyFromFile:p12Path password:@"123456"];
    
    
    NSData *enData = [content dataUsingEncoding:NSUTF8StringEncoding];
    
    //签名
    NSData *signedData = [self.sign sha256WithRSA:enData privateKey:[self.sign getPrivateKeyRefWithP12Path:p12Path]];
   
    //进行base64编码
    NSString * sign = [GTMBase64 stringByEncodingData:signedData];
    
    NSLog(@"签名后的值:%@",sign);
    
    return sign;
}



-(NSString *)getTime{
    // 实例化NSDateFormatter
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    // 设置日期格式
    [formatter setDateFormat:@"yyyy-mm-dd HH:mm:ss"];
    // 获取当前日期
    NSDate *currentDate = [NSDate date];
    
    NSString *currentDateString = [formatter stringFromDate:currentDate];
    
    NSLog(@"%@", currentDateString);
    
    return currentDateString;
}

- (IBAction)submit:(id)sender {
    
    [self signatureAndPost];
}



- (void)test {
    NSString *derPath = [[NSBundle mainBundle] pathForResource:@"public_key" ofType:@"der"];
    [self.rsaCryptor loadPublicKeyFromFile:derPath];
    
    NSString *p12Path = [[NSBundle mainBundle] pathForResource:@"private_key" ofType:@"p12"];
    [self.rsaCryptor loadPrivateKeyFromFile:p12Path password:@"123456"];
    
    NSString *enStr = @"哈哈哈哈哈";
    
    //加解密
    NSData *enData = [self.rsaCryptor rsaEncryptData:
                      [enStr dataUsingEncoding:NSUTF8StringEncoding]];
    NSData *deData = [self.rsaCryptor rsaDecryptData:enData];
    
    
    NSString *deStr = [[NSString alloc] initWithData:deData encoding:NSUTF8StringEncoding];
    
    NSLog(@"加密前： %@ \n 解密后： %@",enStr,deStr);
    
    //签名
    NSData *signedData = [self.sign sha256WithRSA:enData privateKey:[self.sign getPrivateKeyRefWithP12Path:p12Path]];
    NSLog(@"签名后的base64:%@",[GTMBase64 stringByEncodingData:signedData]);
    
    BOOL signSuccess = [self.rsaCryptor rsaSHA256VerifyData:enData withSignature:signedData];
    NSLog(@"是否签名成功：%@",signSuccess ? @"是":@"否");
}

@end
