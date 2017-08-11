//
//  HttpBase.h
//  rsademo
//
//  Created by luffyjet on 2017/8/4.
//  Copyright © 2017年 luffyjet. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AFNetworking.h>

@interface HttpBase : NSObject
+(void)GET:(NSString *)url params:(NSDictionary *)params progress:(void(^)(NSProgress *downloadProgress)) progress success:(void(^)(id data,NSURLResponse *response)) success
      fail:(void(^)(NSError *error)) fail;

+(void)POST:(NSString *_Nonnull)url params:(NSDictionary *_Nullable)params success:(void(^)(id  data,NSURLResponse *response)) success
      fail:(void(^)(NSError *error)) fail;
@end
