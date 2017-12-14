//
//  JMVideoBubbleContentView.h
//  JMessageOCDemo
//
//  Created by oshumini on 2017/11/6.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JMessageOCDemo-Swift.h"

@interface JMVideoBubbleContentView : UIView <IMUIMessageContentViewProtocol>
- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message;
@end
