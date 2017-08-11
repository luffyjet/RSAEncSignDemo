//
//  XYSHASign.m
//  XYRSACryptor
//
//  Created by Panda on 15/11/8.
//  Copyright © 2015年 Panda. All rights reserved.
//

#import "XYSHASign.h"
#import <CommonCrypto/CommonDigest.h>

@implementation XYSHASign

- (SecKeyRef )getPrivateKeyRefWithP12Path:(NSString *)path {
    
    NSData *p12Data = [NSData dataWithContentsOfFile:path];
    
    NSMutableDictionary *options = [[NSMutableDictionary alloc] init];
    
    SecKeyRef privateKeyRef = NULL;
    
    if (!self.passwordBlock)
        NSAssert(NO, @"Please set your p12 password in passwordBlock");
    NSString *passwordStr = self.passwordBlock();
    
    //change to the actual password you used here
    [options setObject:passwordStr forKey:(id)kSecImportExportPassphrase];
    
    CFArrayRef items = CFArrayCreate(NULL, 0, 0, NULL);
    
    OSStatus securityError = SecPKCS12Import((CFDataRef) p12Data,
                                             (CFDictionaryRef)options, &items);
    
    if (securityError == noErr && CFArrayGetCount(items) > 0) {
        CFDictionaryRef identityDict = CFArrayGetValueAtIndex(items, 0);
        SecIdentityRef identityApp =
        (SecIdentityRef)CFDictionaryGetValue(identityDict,
                                             kSecImportItemIdentity);
        
        securityError = SecIdentityCopyPrivateKey(identityApp, &privateKeyRef);
        if (securityError != noErr) {
            privateKeyRef = NULL;
        }
    }
    CFRelease(items);
    
    return privateKeyRef;
}

- (SecKeyRef)getPublicKeyRefWithDerPath:(NSString *)path {
    
    NSData *certData = [NSData dataWithContentsOfFile:path];
    SecCertificateRef cert = SecCertificateCreateWithData(NULL, (CFDataRef)certData);
    SecKeyRef key = NULL;
    SecTrustRef trust = NULL;
    SecPolicyRef policy = NULL;
    
    if (cert != NULL) {
        policy = SecPolicyCreateBasicX509();
        if (policy) {
            if (SecTrustCreateWithCertificates((CFTypeRef)cert, policy, &trust) == noErr) {
                SecTrustResultType result;
                OSStatus res = SecTrustEvaluate(trust, &result);
                
                //MARK:理论上这边是需要对result进行验证的,当kSecTrustResultInvalid，kSecTrustResultDeny，kSecTrustResultFatalTrustFailure时,则无法处理,但是不知为何此result不可靠
                //                if (result == kSecTrustResultInvalid || result == kSecTrustResultRecoverableTrustFailure || result ==kSecTrustResultDeny) {
                //                    return NULL;
                //                }
                
                if (res == noErr) {
                    key = SecTrustCopyPublicKey(trust);
                }
            }
        }
    }
    if (policy) CFRelease(policy);
    if (trust) CFRelease(trust);
    if (cert) CFRelease(cert);
    return key;
}

- (NSData *)sha256WithRSA:(NSData *)plainData privateKey:(SecKeyRef)privateKey {
    
    size_t signedHashBytesSize = SecKeyGetBlockSize(privateKey);
    uint8_t* signedHashBytes = malloc(signedHashBytesSize);
    memset(signedHashBytes, 0x0, signedHashBytesSize);
    
    size_t hashBytesSize = CC_SHA256_DIGEST_LENGTH;
    uint8_t* hashBytes = malloc(hashBytesSize);
    if (!CC_SHA256([plainData bytes], (CC_LONG)[plainData length], hashBytes)) {
        return nil;
    }
    
    SecKeyRawSign(privateKey,
                  kSecPaddingPKCS1SHA256,
                  hashBytes,
                  hashBytesSize,
                  signedHashBytes,
                  &signedHashBytesSize);
    
    NSData* signedHash = [NSData dataWithBytes:signedHashBytes
                                        length:(NSUInteger)signedHashBytesSize];
    
    if (hashBytes)
        free(hashBytes);
    if (signedHashBytes)
        free(signedHashBytes);
    
    return signedHash;
}

- (BOOL)verifySha256WithRSA:(NSData *)plainData signatureData:(NSData *)signatureData publicKey:(SecKeyRef)publicKey {
    
    size_t signedHashBytesSize = SecKeyGetBlockSize(publicKey);
    const void* signedHashBytes = [signatureData bytes];
    
    size_t hashBytesSize = CC_SHA256_DIGEST_LENGTH;
    uint8_t* hashBytes = malloc(hashBytesSize);
    if (!CC_SHA256([plainData bytes], (CC_LONG)[plainData length], hashBytes)) {
        return NO;
    }
    
    OSStatus status = SecKeyRawVerify(publicKey,
                                      kSecPaddingPKCS1SHA256,
                                      hashBytes,
                                      hashBytesSize,
                                      signedHashBytes,
                                      signedHashBytesSize);
    
    return status == errSecSuccess;
}

@end
