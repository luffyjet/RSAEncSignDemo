//
//  XYRSACryptor.h
//  XYRSACryptor
//
//  Created by Panda on 15/11/8.
//  Copyright © 2015年 Panda. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface XYRSACryptor : NSObject


- (void)loadPublicKeyFromFile:(NSString*)derFilePath;
- (void)loadPublicKeyFromData:(NSData*)derData;

- (void)loadPrivateKeyFromFile:(NSString*)p12FilePath password:(NSString*)p12Password;
- (void)loadPrivateKeyFromData:(NSData*)p12Data password:(NSString*)p12Password;

- (SecKeyRef)getPublicKeyRefrenceFromeData:(NSData*)derData;
- (SecKeyRef)getPrivateKeyRefrenceFromData:(NSData*)p12Data password:(NSString*)password;

- (NSString*)rsaEncryptString:(NSString*)string;
- (NSData*)rsaEncryptData:(NSData*)data;

- (NSString*)rsaDecryptString:(NSString*)string;
- (NSData*)rsaDecryptData:(NSData*)data;

- (BOOL)rsaSHA256VerifyData:(NSData *)plainData withSignature:(NSData *)signature;

@end
