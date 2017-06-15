//
//  JMVoiceBubbleContentView.h
//  JMessageOCDemo
//
//  Created by oshumini on 2017/6/14.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JMessageOCDemo-Swift.h"

@interface JMVoiceBubbleContentView : UIView <IMUIMessageContentViewProtocal>
- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message;
@end
