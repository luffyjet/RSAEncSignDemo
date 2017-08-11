//
//  XYSHASign.h
//  XYRSACryptor
//
//  Created by Panda on 15/11/8.
//  Copyright © 2015年 Panda. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef NSString * (^PasswordBlock) ();


@interface XYSHASign : NSObject

@property (nonatomic, copy) PasswordBlock passwordBlock;


- (SecKeyRef)getPrivateKeyRefWithP12Path:(NSString *)path;


- (SecKeyRef)getPublicKeyRefWithDerPath:(NSString *)path;


- (NSData *)sha256WithRSA:(NSData *)plainData privateKey:(SecKeyRef)privateKey;

- (BOOL)verifySha256WithRSA:(NSData *)plainData signatureData:(NSData *)signatureData publicKey:(SecKeyRef)publicKey;

@end
