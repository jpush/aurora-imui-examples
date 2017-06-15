//
//  JMTextBubbleContentView.m
//  JMessage-AuroraIMUI-OC-Demo
//
//  Created by oshumini on 2017/6/12.
//  Copyright © 2017年 HXHG. All rights reserved.
//


#import "JMTextBubbleContentView.h"
#import "MessageModel.h"

@interface JMTextBubbleContentView()
@property(strong, nonatomic)UILabel *textMessageLabel;
@end
//open static var outGoingTextColor = UIColor(netHex: 0x7587A8)
//open static var inComingTextColor = UIColor.white
@implementation JMTextBubbleContentView

- (instancetype)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
    _textMessageLabel = [UILabel new];
    _textMessageLabel.numberOfLines = 0;
    [self addSubview:_textMessageLabel];
  }
  return self;
}

- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message {
  MessageModel *messageModel = (MessageModel *)message;
  _textMessageLabel.text = messageModel.text;
  
  _textMessageLabel.frame = CGRectMake(0, 0, messageModel.layout.bubbleContentSize.width, messageModel.layout.bubbleContentSize.height);
  
  if (message.isOutGoing) {
    _textMessageLabel.textColor = [[UIColor alloc] initWithNetHex: 0x7587A8];
  } else {
    _textMessageLabel.textColor = [UIColor whiteColor];
  }
}

@end
