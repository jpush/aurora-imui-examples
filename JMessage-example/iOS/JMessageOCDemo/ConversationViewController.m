//
//  ConversationViewController.m
//  JMessage-AuroraIMUI-OC-Demo
//
//  Created by oshumini on 2017/6/6.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import "ConversationViewController.h"
//#import <AuroraIMUI/AuroraIMUI-Swift.h>
#import "JMessageOCDemo-Swift.h"
#import <Photos/Photos.h>
#import <AVFoundation/AVFoundation.h>
#import "MessageModel.h"
#import <MediaPlayer/MediaPlayer.h>

@interface ConversationViewController() <IMUIInputViewDelegate, IMUIMessageMessageCollectionViewDelegate, JMessageDelegate>
@property (weak, nonatomic) IBOutlet IMUIMessageCollectionView *messageList;
@property (weak, nonatomic) IBOutlet IMUIInputView *imuiInputView;
@property (strong, nonatomic) AVPlayer *player;
@property (strong, nonatomic) AVPlayerLayer *playLayer;
@end

@implementation ConversationViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  _messageList.delegate = self;
  _imuiInputView.inputViewDelegate = self;
  NSArray *messageArray = [_conversation messageArrayFromNewestWithOffset:@(0) limit:@(20)];
  NSMutableArray *messageModelArray = @[].mutableCopy;
  
  // you can insert your history message
  for (JMSGMessage *message in messageArray) {
    MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
    [messageModelArray addObject:messageModel];
  }
  [JMessage addDelegate:self withConversation:_conversation];
//  [_messageList insertMessagesWith:messageModelArray];
}

- (void)viewDidLayoutSubviews {
    [_messageList scrollToBottomWith:true];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


// - MARK: IMUIInputViewDelegate
- (void)messageCollectionView:(UICollectionView * _Nonnull)willBeginDragging {
  [_imuiInputView hideFeatureView];
}


// - MARK: IMUIInputViewDelegate
/// Tells the delegate that user tap send button and text input string is not empty
- (void)sendTextMessage:(NSString * _Nonnull)messageText {
  JMSGTextContent *content = [[JMSGTextContent alloc] initWithText: messageText];
  JMSGMessage *message = [_conversation createMessageWithContent: content];
  MessageModel *messageModel = [[MessageModel alloc] initWithMessage: message];
  [_conversation sendMessage:message];
  [_messageList appendMessageWith: messageModel];
}
/// Tells the delegate that IMUIInputView will switch to recording voice mode
- (void)switchToMicrophoneModeWithRecordVoiceBtn:(UIButton * _Nonnull)recordVoiceBtn {

}
/// Tells the delegate that start record voice
- (void)startRecordVoice {

}
/// Tells the delegate when finish record voice
- (void)finishRecordVoice:(NSString * _Nonnull)voicePath durationTime:(double)durationTime {
  NSData *voiceData = [NSData dataWithContentsOfFile:voicePath];
  JMSGVoiceContent *voiceContent = [[JMSGVoiceContent alloc] initWithVoiceData:voiceData voiceDuration:@(durationTime)];
  
  if (voiceContent != nil) {
    JMSGMessage *message = [_conversation createMessageWithContent:voiceContent];
    MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
    [_conversation sendMessage:message];
    [_messageList appendMessageWith:messageModel];
    
  }
  
  [self removeFile: voicePath];
}
/// Tells the delegate that user cancel record
- (void)cancelRecordVoice {

}
/// Tells the delegate that IMUIInputView will switch to gallery
- (void)switchToGalleryModeWithPhotoBtn:(UIButton * _Nonnull)photoBtn {

}
/// Tells the delegate that user did selected Photo in gallery
- (void)didSeletedGalleryWithAssetArr:(NSArray<PHAsset *> * _Nonnull)AssetArr {
  for (PHAsset *asset in AssetArr) {
    switch (asset.mediaType) {
      case PHAssetMediaTypeImage: {
        
        PHImageRequestOptions *options = [[PHImageRequestOptions alloc]init];
        options.synchronous  = YES;
        [[PHImageManager defaultManager]
               requestImageForAsset: asset
                         targetSize: CGSizeMake(100.0, 100.0)
                        contentMode:PHImageContentModeAspectFill
                            options:options resultHandler:^(UIImage * _Nullable result, NSDictionary * _Nullable info) {
                              NSData *imageData = UIImagePNGRepresentation(result);
                              JMSGImageContent *imageContent = [[JMSGImageContent alloc] initWithImageData:imageData];
                              JMSGMessage *message = [_conversation createMessageWithContent:imageContent];
                              MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
                              [_conversation sendMessage:message];
                              [_messageList appendMessageWith:messageModel];
                                      }];
        break;
      }
        
      default:
        break;
    }
  }
}
/// Tells the delegate that IMUIInputView will switch to camera mode
- (void)switchToCameraModeWithCameraBtn:(UIButton * _Nonnull)cameraBtn {

}
/// Tells the delegate that user did shoot picture in camera mode
- (void)didShootPictureWithPicture:(NSData * _Nonnull)picture {
  JMSGImageContent *imageContent = [[JMSGImageContent alloc] initWithImageData:picture];
  JMSGMessage *message = [_conversation createMessageWithContent:imageContent];
  MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
  [_conversation sendMessage:message];
  [_messageList appendMessageWith:messageModel];
  
}
/// Tells the delegate when starting record video
- (void)startRecordVideo {

}
/// Tells the delegate when user did shoot video in camera mode
- (void)finishRecordVideoWithVideoPath:(NSString * _Nonnull)videoPath durationTime:(double)durationTime {
  NSData *data = [NSData dataWithContentsOfFile: videoPath];
  
  JMSGFileContent *content = [[JMSGFileContent alloc] initWithFileData:data fileName:@"video"];
  content.format = @"mp4";
  JMSGMessage *message = [_conversation createMessageWithContent: content];
  JMSGFileContent *fileContent = message.content;
  NSString *path = fileContent.originMediaLocalPath;
  
  MessageModel *messageModel = [[MessageModel alloc] initWithMessage: message];
//  messageModel.mediaFilePath = videoPath;

  
  [_conversation sendMessage:message];
  
  [_messageList appendMessageWith:messageModel];

  //get video image
//  AVURLAsset *asset = [AVURLAsset URLAssetWithURL:[NSURL fileURLWithPath:messageModel.mediaFilePath] options:nil];
//  AVAssetImageGenerator *imgGenerator = [AVAssetImageGenerator assetImageGeneratorWithAsset:asset];
//  NSError *error = nil;
//  imgGenerator.appliesPreferredTrackTransform = YES;
//  CGImageRef *cgImg = [imgGenerator copyCGImageAtTime:CMTimeMake(0, 1) actualTime:nil error: &error];
//  UIImage *img = [UIImage imageWithCGImage:cgImg];

//  BOOL isExist = [NSFileManager.defaultManager fileExistsAtPath:messageModel.mediaFilePath];
//  NSData *thedata = [NSData dataWithContentsOfURL:[NSURL fileURLWithPath:messageModel.mediaFilePath]];
//
//  NSError *error = nil;
//  NSString *copyPath = [NSString stringWithFormat:@"%@.mp4",messageModel.mediaFilePath];
//  [NSFileManager.defaultManager copyItemAtPath:messageModel.mediaFilePath toPath:copyPath error:&error];
//  MPMoviePlayerViewController *vc = [[MPMoviePlayerViewController alloc] initWithContentURL:[NSURL fileURLWithPath:copyPath]];
//  [[MPMoviePlayerController alloc] ]
//  [self presentViewController:vc animated:true completion:^{
//      [vc.moviePlayer play];
//  }];
  
  
}

- (void)keyBoardWillShowWithHeight:(CGFloat)height durationTime:(double)durationTime {

}

- (void)removeFile:(NSString *)filePath
{
  NSFileManager *fileManager = [NSFileManager defaultManager];
  NSError *error;
  BOOL success = [fileManager removeItemAtPath:filePath error:&error];
  if (success) {
    UIAlertView *removedSuccessFullyAlert = [[UIAlertView alloc] initWithTitle:@"Congratulations:" message:@"Successfully removed" delegate:self cancelButtonTitle:@"Close" otherButtonTitles:nil];
    [removedSuccessFullyAlert show];
  } else {
    NSLog(@"Could not delete file -:%@ ",[error localizedDescription]);
  }
}

- (void)onSendMessageResponse:(JMSGMessage *)message
                        error:(NSError *)error {
  if (error == nil) {
//    MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
//    [_messageList updateMessageWith:messageModel];
  }
}


- (void)onReceiveMessage:(JMSGMessage *)message
                   error:(NSError *)error {
//  if (error == nil) {
//    MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
//    [_messageList appendMessageWith:messageModel];
//  }
}

- (void)onReceiveMessageDownloadFailed:(JMSGMessage *)message {
  MessageModel *messageModel = [[MessageModel alloc] initWithMessage:message];
  [_messageList updateMessageWith:messageModel];
}
@end
