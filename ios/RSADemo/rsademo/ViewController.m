//
//  ViewController.m
//  rsademo
//
//  Created by luffyjet on 2017/8/4.
//  Copyright © 2017年 luffyjet. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *passTF;
@property (weak, nonatomic) IBOutlet UITextField *nameTF;
- (IBAction)submit:(id)sender;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)submit:(id)sender {
    NSString *uname = [_nameTF text];
    NSString *pass = [_passTF text];
    
        //使用公钥加密密码
        NSString *enc = [RSAEncryptor encryptString:pass publicKey: kPublicKeyInRSA];
        
        NSLog(@"enc： %@",enc);
        
        //将加密后的数据提交后台
        [self rsaPost2server:uname encStr:enc];
}


-(void)rsaPost2server:(NSString *)uname encStr:(NSString *) enc{
    
    NSDictionary *params = @{ @"username":uname,  @"password":enc };
    
    [HttpBase POST:[kBaseUrl stringByAppendingString:@"/rsa/decode"] params:params success:^(id data, NSURLResponse *response) {
        
        NSLog(@"decode: %@",data);
        
        UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"解密的密码为" message:data[@"data"] preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {}];
        
        [alert addAction:defaultAction];
        
        [self presentViewController:alert animated:YES completion:nil];
    } fail:^(NSError *error) {
        NSLog(@"%@",[error  localizedFailureReason]);
    }];

}

@end
