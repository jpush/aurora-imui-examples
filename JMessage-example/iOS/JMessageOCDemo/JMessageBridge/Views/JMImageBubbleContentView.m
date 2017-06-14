//
//  JMImageBubbleContentView.m
//  JMessageOCDemo
//
//  Created by oshumini on 2017/6/14.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import "JMImageBubbleContentView.h"
#import "MessageModel.h"



@interface JMImageBubbleContentView ()
@property(strong, nonatomic)UIImageView *imageMessageView;
@property(strong, nonatomic)NSString *msgId;
@end


@implementation JMImageBubbleContentView

- (instancetype)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
    _imageMessageView = [UIImageView new];
    [self addSubview:_imageMessageView];
  }
  return self;
}

- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message {
  MessageModel *messageModel = (MessageModel *)message;
  _msgId = messageModel.msgId;
  
  _imageMessageView.frame = CGRectMake(0, 0, messageModel.layout.bubbleContentSize.width, messageModel.layout.bubbleContentSize.height);
  
  [messageModel getMediaDataCallback:^(NSData *data, NSString *msgId) {
    if ([msgId isEqualToString:_msgId]) {
      UIImage *image = [UIImage imageWithData: data];
      _imageMessageView.image = image;
    }
  }];
}

@end
