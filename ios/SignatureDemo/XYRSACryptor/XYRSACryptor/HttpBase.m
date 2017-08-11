//
//  HttpBase.m
//  rsademo
//
//  Created by luffyjet on 2017/8/4.
//  Copyright © 2017年 luffyjet. All rights reserved.
//

#import "HttpBase.h"

@implementation HttpBase
+(void)GET:(NSString *)url params:(NSDictionary *)params progress:(void(^)(NSProgress * _Nonnull downloadProgress)) progress success:(void(^)(id data,NSURLResponse *response)) success
      fail:(void(^)(NSError *error)) fail{
    
    [[AFHTTPSessionManager manager] GET:url parameters:params progress:^(NSProgress * _Nonnull downloadProgress) {
        progress(downloadProgress);
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        success(responseObject,task==nil?nil:[task response]);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        fail(error);
    }];
}

+(void)POST:(NSString *)url params:(NSDictionary *)params success:(void(^)(id data,NSURLResponse *response)) success
       fail:(void(^)(NSError *error)) fail{
    [[AFHTTPSessionManager manager] POST:url parameters:params progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        success(responseObject,task==nil?nil:[task response]);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        fail(error);
    }];
}
@end
