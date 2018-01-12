//
//  MessageModel.m
//  JMessage-AuroraIMUI-OC-Demo
//
//  Created by oshumini on 2017/6/6.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import "MessageModel.h"
#import "MessageLayout.h"
#import <CoreGraphics/CoreGraphics.h>
#import "UserModel.h"

@interface MessageModel()
@property(strong, nonatomic)NSString *messageText;
@property(strong, nonatomic)NSString *messagemediaPath;
@property(strong, nonatomic)JMSGMessage *message;

@end

@implementation MessageModel
- (instancetype)init
{
  self = [super init];
  if (self) {
    
  }
  return self;
}

- (NSString *)getText {
  return _messageText;
}

- (NSString *)text {
  return _messageText;
}

- (NSString *)getMediaPath {
  return _messagemediaPath;
}

- (NSString *)mediaFilePath {
  return _messagemediaPath;
}

- (NSString *)setMediaFilePath:(NSString *)path {
  return _messagemediaPath = path;
}
- (void)getMediaDataCallback:(JMAsyncDataHandler) callback {
  switch (_message.contentType) {
    case kJMSGContentTypeText: {
      break;
    }
    case kJMSGContentTypeImage: {
      JMSGImageContent *content = _message.content;
      [content thumbImageData:^(NSData *data, NSString *objectId, NSError *error) {
        if(error == nil) {
          callback(data,_message.msgId);
          
        }
      }];
      break;
    }
      
    case kJMSGContentTypeVoice: {
      JMSGVoiceContent *content = _message.content;
      [content voiceData:^(NSData *data, NSString *objectId, NSError *error) {
        if(error == nil) {
          callback(data,_message.msgId);
        }
      }];
      break;
    }
      
    default:
      break;
  }
}

- (UIImage *)resizableBubbleImage {
  UIImage *bubbleImg = nil;
  
  if (_isOutGoing) {
    bubbleImg = [UIImage imageNamed:@"outGoing_bubble"];
    bubbleImg = [bubbleImg resizableImageWithCapInsets:UIEdgeInsetsMake(24, 10, 9, 15) resizingMode:UIImageResizingModeTile];
  } else {
    bubbleImg = [UIImage imageNamed:@"inComing_bubble"];
    bubbleImg = [bubbleImg resizableImageWithCapInsets:UIEdgeInsetsMake(24, 15, 9, 10) resizingMode:UIImageResizingModeTile];
  }
  return bubbleImg;
}


- (instancetype)initWithMessage:(JMSGMessage *)message {
  
  self = [super init];
  if (self) {
    _msgId = message.msgId;
    _fromUser = [UserModel new];
//    _timeString = @"";
    _isOutGoing = !message.isReceived;
    _message = message;
    switch (message.contentType) {
      case kJMSGContentTypeText: {
        _type = @"Text";
        JMSGTextContent *textContent = (JMSGTextContent *)message.content;
        UIEdgeInsets contentInset = UIEdgeInsetsZero;
        _messageText = textContent.text;
        
        if (_isOutGoing) {
          contentInset = UIEdgeInsetsMake(10, 10, 10, 10);
        } else {
          contentInset = UIEdgeInsetsMake(10, 15, 10, 10);
        }
        _layout = [[MessageLayout alloc] initWithIsOutGoingMessage:_isOutGoing
                                                    isNeedShowTime:false
                                                 bubbleContentSize:[MessageModel calculateTextContentSizeWithText: textContent.text]
                                               bubbleContentInsets:contentInset
                                                       contentType: @"Text"];
        break;
      }
      case kJMSGContentTypeImage: {
        _type = @"Image";
        
        _layout = [[MessageLayout alloc] initWithIsOutGoingMessage: _isOutGoing
                                                    isNeedShowTime: false
                                                 bubbleContentSize: CGSizeMake(120, 160)
                                               bubbleContentInsets: UIEdgeInsetsZero
                                                       contentType: @"Image"];
        
        break;
      }
        
      case kJMSGContentTypeVoice: {
        _type = @"Voice";
        _layout = [[MessageLayout alloc] initWithIsOutGoingMessage: _isOutGoing
                                                    isNeedShowTime: false
                                                 bubbleContentSize: CGSizeMake(80, 37)
                                               bubbleContentInsets: UIEdgeInsetsZero
                                                       contentType: @"Voice"];
        break;
      }
      case kJMSGContentTypeFile: {
        _type = @"Video";
        JMSGFileContent *content = (JMSGFileContent *)message.content;
        [content fileData:^(NSData *data, NSString *objectId, NSError *error) {
          
        }];
        _messagemediaPath = content.originMediaLocalPath;
        
        _layout = [[MessageLayout alloc] initWithIsOutGoingMessage: _isOutGoing
                                                    isNeedShowTime: false
                                                 bubbleContentSize: CGSizeMake(120, 160)
                                               bubbleContentInsets: UIEdgeInsetsZero
                                                       contentType: @"Video"];
      }
      default:
        break;
    }
    

    switch (message.status) {
      case kJMSGMessageStatusReceiveSucceed:
        _messageStatus = IMUIMessageStatusSuccess;
        break;
      case kJMSGMessageStatusSendSucceed:
        _messageStatus = IMUIMessageStatusSuccess;
        break;
      case kJMSGMessageStatusSending:
        _messageStatus = IMUIMessageStatusSending;
        break;
      case kJMSGMessageStatusReceiving:
        _messageStatus = IMUIMessageStatusSending;
        break;
      case kJMSGMessageStatusSendUploadSucceed:
        _messageStatus = IMUIMessageStatusSuccess;
        break;
      case kJMSGMessageStatusSendDraft:
        _messageStatus = IMUIMessageStatusSuccess;
        break;
        
      default:
        _messageStatus = IMUIMessageStatusMediaDownloadFail;
        break;
    }
  }
  _messageStatus = IMUIMessageStatusMediaDownloadFail;
  return self;
}

+ (CGSize)calculateTextContentSizeWithText:(NSString *)text {
  return [MessageModel getTextSizeWithString:text maxWidth: IMUIMessageCellLayout.bubbleMaxWidth];
}

+ (CGSize)getTextSizeWithString:(NSString *)string maxWidth:(CGFloat)maxWidth {
  CGSize maxSize = CGSizeMake(maxWidth, 2000);
  UIFont *font =[UIFont systemFontOfSize:18];
  NSMutableParagraphStyle *paragraphStyle= [[NSMutableParagraphStyle alloc] init];
  CGSize realSize = [string boundingRectWithSize:maxSize options:NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:@{NSFontAttributeName:font,NSParagraphStyleAttributeName: paragraphStyle} context: nil].size;
  CGSize imgSize =realSize;

  return imgSize;
}

@end
