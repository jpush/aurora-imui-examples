//
//  JMTextBubbleContentView.h
//  JMessage-AuroraIMUI-OC-Demo
//
//  Created by oshumini on 2017/6/12.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import <UIKit/UIKit.h>
//#import <AuroraIMUI/AuroraIMUI-Swift.h>
#import "JMessageOCDemo-Swift.h"

@interface JMTextBubbleContentView : UIView <IMUIMessageContentViewProtocal>
- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message;
@end
