//
//  XYRSACryptor.m
//  XYRSACryptor
//
//  Created by Panda on 15/11/8.
//  Copyright © 2015年 Panda. All rights reserved.
//

#import "XYRSACryptor.h"
#import "GTMBase64.h"
#import <CommonCrypto/CommonCrypto.h>
#import <Security/Security.h>

@implementation XYRSACryptor {
    SecKeyRef publicKey;
    SecKeyRef privateKey;
}

#pragma mark - Private Methods

- (void)dealloc {
    if (publicKey)
        CFRelease(publicKey);
    
    if (!privateKey)
        CFRelease(privateKey);
}

- (SecKeyRef)getPublicKey {
    return publicKey;
}

- (SecKeyRef)getPrivatKey {
    return privateKey;
}

#pragma mark - Public Methods

- (void)loadPublicKeyFromFile:(NSString*)derFilePath {

    NSData *derData = [[NSData alloc] initWithContentsOfFile:derFilePath];
    [self loadPublicKeyFromData:derData];
}

- (void)loadPublicKeyFromData:(NSData*)derData {
    publicKey = [self getPublicKeyRefrenceFromeData: derData];
}

- (void)loadPrivateKeyFromFile:(NSString*)p12FilePath password:(NSString*)p12Password {

    NSData *p12Data = [NSData dataWithContentsOfFile:p12FilePath];
    [self loadPrivateKeyFromData: p12Data password:p12Password];
}

- (void)loadPrivateKeyFromData:(NSData*)p12Data password:(NSString*)p12Password {
    privateKey = [self getPrivateKeyRefrenceFromData: p12Data password: p12Password];
}

- (SecKeyRef)getPublicKeyRefrenceFromeData:(NSData*)derData {
    
    SecCertificateRef myCertificate = SecCertificateCreateWithData(kCFAllocatorDefault, (__bridge CFDataRef)derData);
    SecPolicyRef myPolicy = SecPolicyCreateBasicX509();
    SecTrustRef myTrust;
    OSStatus status = SecTrustCreateWithCertificates(myCertificate,myPolicy,&myTrust);
    SecTrustResultType trustResult;
    if (status == noErr) {
        status = SecTrustEvaluate(myTrust, &trustResult);
    }
    SecKeyRef securityKey = SecTrustCopyPublicKey(myTrust);
    CFRelease(myCertificate);
    CFRelease(myPolicy);
    CFRelease(myTrust);
    
    return securityKey;
}

- (SecKeyRef)getPrivateKeyRefrenceFromData:(NSData*)p12Data password:(NSString*)password {
 
    SecKeyRef privateKeyRef = NULL;
    NSMutableDictionary * options = [[NSMutableDictionary alloc] init];
    [options setObject: password forKey:(__bridge id)kSecImportExportPassphrase];
    CFArrayRef items = CFArrayCreate(NULL, 0, 0, NULL);
    OSStatus securityError = SecPKCS12Import((__bridge CFDataRef) p12Data, (__bridge CFDictionaryRef)options, &items);
    if (securityError == noErr && CFArrayGetCount(items) > 0) {
        CFDictionaryRef identityDict = CFArrayGetValueAtIndex(items, 0);
        SecIdentityRef identityApp = (SecIdentityRef)CFDictionaryGetValue(identityDict, kSecImportItemIdentity);
        securityError = SecIdentityCopyPrivateKey(identityApp, &privateKeyRef);
        if (securityError != noErr) {
            privateKeyRef = NULL;
        }
    }
    CFRelease(items);
    
    return privateKeyRef;
}

#pragma mark  Encrypt

- (NSString*)rsaEncryptString:(NSString*)string {
    
    NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
    NSData* encryptedData = [self rsaEncryptData: data];
    NSString *base64EncryptedString = [GTMBase64 stringByEncodingData:encryptedData];
    return base64EncryptedString;
}


- (NSData*)rsaEncryptData:(NSData*)data {

    SecKeyRef key = [self getPublicKey];
    size_t cipherBufferSize = SecKeyGetBlockSize(key);
    uint8_t *cipherBuffer = malloc(cipherBufferSize * sizeof(uint8_t));
    size_t blockSize = cipherBufferSize - 11;
    size_t blockCount = (size_t)ceil([data length] / (double)blockSize);
    
    NSMutableData *encryptedData = [[NSMutableData alloc] init];
    
    for (int i=0; i<blockCount; i++) {
        unsigned long bufferSize = MIN(blockSize , [data length] - i * blockSize);
        NSData *buffer = [data subdataWithRange:NSMakeRange(i * blockSize, bufferSize)];
        OSStatus status = SecKeyEncrypt(key, kSecPaddingPKCS1, (const uint8_t *)[buffer bytes], [buffer length], cipherBuffer, &cipherBufferSize);

        if (status != noErr) {
            return nil;
        }
        
        NSData *encryptedBytes = [[NSData alloc] initWithBytes:(const void *)cipherBuffer length:cipherBufferSize];
        [encryptedData appendData:encryptedBytes];
    }
    
    if (cipherBuffer){
        free(cipherBuffer);
    }
    
    return encryptedData;
}


#pragma mark  Decrypt

- (NSString*)rsaDecryptString:(NSString*)string {
    
    NSData* data = [[NSData alloc] initWithBase64EncodedString:string options:NSDataBase64DecodingIgnoreUnknownCharacters];
    NSData* decryptData = [self rsaDecryptData:data];
    NSString* result = [[NSString alloc] initWithData:decryptData encoding:NSUTF8StringEncoding];
    return result;
}

- (NSData*)rsaDecryptData:(NSData*)data {
    SecKeyRef key = [self getPrivatKey];
    
    size_t cipherBufferSize = SecKeyGetBlockSize(key);
    size_t blockSize = cipherBufferSize;
    size_t blockCount = (size_t)ceil([data length] / (double)blockSize);

    NSMutableData *decryptedData = [[NSMutableData alloc] init];
    
    for (int i = 0; i < blockCount; i++) {
        unsigned long bufferSize = MIN(blockSize , [data length] - i * blockSize);
        NSData *buffer = [data subdataWithRange:NSMakeRange(i * blockSize, bufferSize)];
     
        size_t cipherLen = [buffer length];
        void *cipher = malloc(cipherLen);
        [buffer getBytes:cipher length:cipherLen];
        size_t plainLen = SecKeyGetBlockSize(key);
        void *plain = malloc(plainLen);
        
        OSStatus status = SecKeyDecrypt(key, kSecPaddingPKCS1, cipher, cipherLen, plain, &plainLen);
        
        if (status != noErr) {
            return nil;
        }
        
        NSData *decryptedBytes = [[NSData alloc] initWithBytes:(const void *)plain length:plainLen];
        [decryptedData appendData:decryptedBytes];
    }
    
    return decryptedData;
}

#pragma mark  Sign

- (NSData *)rsaSHA256SignData:(NSData *)plainData {
   SecKeyRef key = [self getPrivatKey];
    
    size_t signedHashBytesSize = SecKeyGetBlockSize(key);
    uint8_t* signedHashBytes = malloc(signedHashBytesSize);
    memset(signedHashBytes, 0x0, signedHashBytesSize);
    
    size_t hashBytesSize = CC_SHA256_DIGEST_LENGTH;
    uint8_t* hashBytes = malloc(hashBytesSize);
    if (!CC_SHA256([plainData bytes], (CC_LONG)[plainData length], hashBytes)) {
        return nil;
    }
    
    SecKeyRawSign(key,
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

- (BOOL)rsaSHA256VerifyData:(NSData *)plainData withSignature:(NSData *)signature {
    SecKeyRef key = [self getPublicKey];

    size_t signedHashBytesSize = SecKeyGetBlockSize(key);
    const void* signedHashBytes = [signature bytes];
    
    size_t hashBytesSize = CC_SHA256_DIGEST_LENGTH;
    uint8_t* hashBytes = malloc(hashBytesSize);
    if (!CC_SHA256([plainData bytes], (CC_LONG)[plainData length], hashBytes)) {
        return NO;
    }
    
    OSStatus status = SecKeyRawVerify(key,
                                      kSecPaddingPKCS1SHA256,
                                      hashBytes,
                                      hashBytesSize,
                                      signedHashBytes,
                                      signedHashBytesSize);
    
    return status == errSecSuccess;
}

@end
