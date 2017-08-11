# 服务端API加签说明

## 为了各个终端兼容，采用openssl生成密钥对

```
 请安装最新 openssl , 运行 generateRSAKeypair.sh 生成。
 务必记住中途提示输入的密码，iOS需要用到。
 
 Demo 使用的一份密钥文件位于 key 目录
	
```


## 如何签名

1.筛选并排序

获取所有请求参数，不包括字节类型参数，如文件、字节流，剔除sign字段，剔除值为空的参数，并按照第一个字符的键值ASCII码递增排序（字母升序排序），如果遇到相同字符则按照第二个字符的键值ASCII码递增排序，以此类推。

2.拼接

将排序后的参数与其对应值，组合成“参数=参数值”的格式，并且把这些参数用&字符连接起来，此时生成的字符串为待签名字符串。

例如下面的请求示例，参数值都是示例，开发者参考格式即可：

```
REQUEST URL: https://openapi.alipay.com/gateway.do
REQUEST METHOD: POST
CONTENT:
app_id=2014072300007148
method=alipay.mobile.public.menu.add
charset=GBK
sign_type=RSA2
timestamp=2014-07-24 03:07:50
biz_content={"button":[{"actionParam":"ZFB_HFCZ","actionType":"out","name":"话费充值"},{"name":"查询","subButton":[{"actionParam":"ZFB_YECX","actionType":"out","name":"余额查询"},{"actionParam":"ZFB_LLCX","actionType":"out","name":"流量查询"},{"actionParam":"ZFB_HFCX","actionType":"out","name":"话费查询"}]},{"actionParam":"http://m.alipay.com","actionType":"link","name":"最新优惠"}]}
sign=e9zEAe4TTQ4LPLQvETPoLGXTiURcxiAKfMVQ6Hrrsx2hmyIEGvSfAQzbLxHrhyZ48wOJXTsD4FPnt+YGdK57+fP1BCbf9rIVycfjhYCqlFhbTu9pFnZgT55W+xbAFb9y7vL0MyAxwXUXvZtQVqEwW7pURtKilbcBTEW7TAxzgro=
version=1.0
```
则待签名字符串为：

```
app_id=2014072300007148&biz_content={"button":[{"actionParam":"ZFB_HFCZ","actionType":"out","name":"话费充值"},{"name":"查询","subButton":[{"actionParam":"ZFB_YECX","actionType":"out","name":"余额查询"},{"actionParam":"ZFB_LLCX","actionType":"out","name":"流量查询"},{"actionParam":"ZFB_HFCX","actionType":"out","name":"话费查询"}]},{"actionParam":"http://m.alipay.com","actionType":"link","name":"最新优惠"}]}&charset=GBK&method=alipay.mobile.public.menu.add&sign_type=RSA2&timestamp=2014-07-24 03:07:50&version=1.0
```
3.调用签名函数

使用各自语言对应的SHA256WithRSA(对应sign_type为RSA2)或SHA1WithRSA(对应sign_type为RSA)签名函数利用商户私钥对待签名字符串进行签名，并进行Base64编码。

4.把生成的签名赋值给sign参数，拼接到请求参数中，比如。

```
...&sign=DuB3sIXLrFi96WgDQWn048wtlNeqwprT/URnFa4k4DJSmjJjZzFFyKh9LcK0WhSRmvP0k3duFrMVpZMy/q5CYULnl2cIXBdlH4rD6EmCGxMdWcTUTp2I1Z4mHt4+RMyIEU64Xd9nLmId3A4G3xX8dgJek5vaKVoX6BXPMeCz0gI=
```

具体请参考每种客户端的具体DEMO。

## 参数加密
参数加密使用 rsa 公钥，每个项目都有demo

## 前端js加密混淆 
请参考 
http://div.io/topic/1220#

https://www.zhihu.com/question/20306249

## DEMO说明
请参考每个项目具体代码，包含 java server,  web,  android, ios



