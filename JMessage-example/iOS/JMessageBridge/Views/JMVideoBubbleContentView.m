//
//  JMVideoBubbleContentView.m
//  JMessageOCDemo
//
//  Created by oshumini on 2017/11/6.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import "JMVideoBubbleContentView.h"
#import "MessageModel.h"

@interface JMVideoBubbleContentView ()
@property(strong, nonatomic)UIImageView *videoView;
@property(strong, nonatomic)UIButton *playBtn;
@property(strong, nonatomic)UILabel *videoDuration;
@property(weak, nonatomic)MessageModel *messageModel;

@property(assign, nonatomic)BOOL isMediaActivity;
@end

@implementation JMVideoBubbleContentView

- (instancetype)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
    _videoView = [UIImageView new];
    _playBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 56, 56)];
    
    NSString *imgPath = [NSBundle.mainBundle pathForResource:@"IMUIAssets.bundle/image/video_play_btn" ofType:@"png"];
    [_playBtn setImage:[UIImage imageWithContentsOfFile:imgPath] forState:UIControlStateNormal];
    
    _videoDuration = [UILabel new];
    [self addSubview: _videoView];
    [self addSubview: _playBtn];
    [self addSubview: _videoDuration];
    
    _isMediaActivity = NO;
  }
  return self;
}

- (void)layoutContentViewWithMessage:(id <IMUIMessageModelProtocol> _Nonnull)message {
  MessageModel *messageModel = (MessageModel *)message;
  _messageModel = (MessageModel *)message;
  
  _videoView.frame = CGRectMake(0, 0, messageModel.layout.bubbleContentSize.width, messageModel.layout.bubbleContentSize.height);
  _playBtn.center = CGPointMake(_videoView.frame.size.width/2, _videoView.frame.size.height/2);
  CGFloat durationX = _videoView.frame.size.width - 30;
  CGFloat durationY = _videoView.frame.size.height - 24;
  
  _videoDuration.frame = CGRectMake(durationX, durationY, 30, 24);
  [self updateVideoShoot:_messageModel.mediaFilePath];
}

- (void)updateVideoShoot:(NSString *)videoPath {
  dispatch_queue_t serialQueue = dispatch_queue_create("videoLoad", DISPATCH_QUEUE_SERIAL);
  dispatch_async(serialQueue, ^{
    AVURLAsset *asset = [AVURLAsset URLAssetWithURL:[NSURL fileURLWithPath:videoPath ?: @""] options:nil];
    
    // get video shoot
    AVAssetImageGenerator *imgGenerator = [AVAssetImageGenerator assetImageGeneratorWithAsset:asset];
    NSError *error = nil;
    imgGenerator.appliesPreferredTrackTransform = YES;
    CGImageRef cgImg = [imgGenerator copyCGImageAtTime:CMTimeMake(0, 1) actualTime:nil error: &error];
    if (!error) {
      UIImage *img = [UIImage imageWithCGImage:cgImg];
      dispatch_async(dispatch_get_main_queue(), ^{
        self.videoView.image = img;
      });
    } else {
      dispatch_async(dispatch_get_main_queue(), ^{
        self.videoView.image = nil;
      });
    }
  });
}


@end
