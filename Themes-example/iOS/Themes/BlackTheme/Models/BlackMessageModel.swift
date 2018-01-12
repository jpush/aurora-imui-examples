//
//  BlackMessageModel.swift
//  Themes
//
//  Created by oshumini on 2017/12/20.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AuroraIMUI

class BlackMessageModel: IMUIMessageModel {
  open var myTextMessage: String = ""
  
  var mediaPath: String = ""
  
  var isContinuous: Bool = false
  
  override func mediaFilePath() -> String {
    return mediaPath
  }
  
  init(msgId: String,
       messageStatus: IMUIMessageStatus,
       fromUser: MyUser,
       isOutGoing: Bool,
       date: Date,
       type: String,
       text: String,
       mediaPath: String,
       layout: IMUIMessageCellLayoutProtocol,
       duration: CGFloat?,
       isContinuous: Bool) {
    
    self.myTextMessage = text
    self.mediaPath = mediaPath
    self.isContinuous = isContinuous
    
    super.init(msgId: msgId,
               messageStatus: messageStatus,
               fromUser: fromUser,
               isOutGoing: isOutGoing,
               time: "",
               type: type,
               cellLayout: layout,
               duration: duration)
  }
  
  convenience init(text: String, isOutGoing: Bool, isContinuous: Bool = false) {
    
    let myLayout = BlackMessageCellLayout(isOutGoingMessage: isOutGoing,
                                          isNeedShowTime: false,
                                          bubbleContentSize: BlackMessageModel.calculateTextContentSize(text: text),
                                          bubbleContentInsets: UIEdgeInsets.zero,
                                          type: "text",
                                          isContinuous: isContinuous)
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId,
              messageStatus: .failed,
              fromUser: MyUser(),
              isOutGoing: isOutGoing,
              date: Date(),
              type: "text",
              text: text,
              mediaPath: "",
              layout:  myLayout,
              duration: nil,
              isContinuous: isContinuous)
  }
  
  convenience init(voicePath: String, duration: CGFloat, isOutGoing: Bool, isContinuous: Bool = false) {
    let myLayout = BlackMessageCellLayout(isOutGoingMessage: isOutGoing,
                                             isNeedShowTime: false,
                                          bubbleContentSize: CGSize(width: 160, height: 46),
                                          bubbleContentInsets: UIEdgeInsets.zero,
                                          type: "voice",
                                          isContinuous: isContinuous)
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId,
              messageStatus: .sending,
              fromUser: MyUser(),
              isOutGoing: isOutGoing,
              date: Date(),
              type: "voice",
              text: "",
              mediaPath: voicePath,
              layout:  myLayout,
              duration: duration,
              isContinuous: isContinuous)
  }
  
  convenience init(imagePath: String, isOutGoing: Bool, isContinuous: Bool = false) {
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    
    var imgSize = CGSize(width: 120, height: 160)
    if let img = UIImage(contentsOfFile: imagePath) {
      imgSize = BlackMessageModel.converImageSize(with: CGSize(width: (img.cgImage?.width)!,
                                                               height: (img.cgImage?.height)!))
    }
    
    let myLayout = BlackMessageCellLayout(isOutGoingMessage: isOutGoing,
                                          isNeedShowTime: false,
                                          bubbleContentSize: imgSize,
                                          bubbleContentInsets: UIEdgeInsets.zero,
                                          type: "image",
                                          isContinuous: isContinuous)
    self.init(msgId: msgId,
              messageStatus: .sending,
              fromUser: MyUser(),
              isOutGoing: isOutGoing,
              date: Date(),
              type: "image",
              text: "",
              mediaPath: imagePath,
              layout:  myLayout,
              duration: nil,
              isContinuous: isContinuous)
  }
  
  convenience init(videoPath: String, isOutGoing: Bool, isContinuous: Bool = false) {
    let myLayout = BlackMessageCellLayout(isOutGoingMessage: isOutGoing,
                                          isNeedShowTime: false,
                                          bubbleContentSize: CGSize(width: 120, height: 160),
                                          bubbleContentInsets: UIEdgeInsets.zero,
                                          type: "video",
                                          isContinuous: isContinuous)
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId,
              messageStatus: .sending,
              fromUser: MyUser(),
              isOutGoing: isOutGoing,
              date: Date(),
              type: "video",
              text: "",
              mediaPath: videoPath,
              layout: myLayout,
              duration: nil,
              isContinuous: isContinuous)
  }
  
  override func text() -> String {
    return self.myTextMessage
  }
  
  override var resizableBubbleImage: UIImage {
    var bubbleImg: UIImage?
    if isOutGoing {
      bubbleImg = UIImage(named: "outGoing_black_bubble")
      bubbleImg = bubbleImg?.resizableImage(withCapInsets: UIEdgeInsetsMake(14, 14, 14, 14), resizingMode: .tile)
    } else {
      bubbleImg = UIImage(named: "inComing_black_bubble")
      bubbleImg = bubbleImg?.resizableImage(withCapInsets: UIEdgeInsetsMake(14, 14, 14, 14), resizingMode: .tile)
    }
    
    return bubbleImg!
  }
  
  static func calculateTextContentSize(text: String) -> CGSize {
    let textSize  = text.sizeWithConstrainedWidth(with: IMUIMessageCellLayout.bubbleMaxWidth,
                                                  font: UIFont.systemFont(ofSize: 18))
    
    return textSize
  }
  
  static func converImageSize(with size: CGSize) -> CGSize {
    let maxSide = 160.0
    
    var scale = size.width / size.height
    
    if size.width > size.height {
      scale = scale > 2 ? 2 : scale
      return CGSize(width: CGFloat(maxSide), height: CGFloat(maxSide) / CGFloat(scale))
    } else {
      scale = scale < 0.5 ? 0.5 : scale
      return CGSize(width: CGFloat(maxSide) * CGFloat(scale), height: CGFloat(maxSide))
    }
  }
}


//MARK - IMUIMessageCellLayoutProtocal
class BlackMessageCellLayout: IMUIMessageCellLayout {
  
  var type: String
  var isContinuous: Bool = false
  
  init(isOutGoingMessage: Bool,
       isNeedShowTime: Bool,
       bubbleContentSize: CGSize,
       bubbleContentInsets: UIEdgeInsets,
       type: String,
       isContinuous: Bool) {
    self.type = type
    self.isContinuous = isContinuous
    
    super.init(isOutGoingMessage: isOutGoingMessage,
               isNeedShowTime: isNeedShowTime,
               bubbleContentSize: bubbleContentSize,
               bubbleContentInsets: UIEdgeInsets.zero)
  }
  
  override public var bubbleContentInset: UIEdgeInsets {
    if type != "text" { return UIEdgeInsets.zero }
    if isOutGoingMessage {
      return UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 15)
    } else {
      return UIEdgeInsets(top: 10, left: 15, bottom: 10, right: 10)
    }
  }
  
  override public var bubbleContentView: IMUIMessageContentViewProtocol {
    if type == "text" {
      return IMUITextMessageContentView()
    }
    
    if type == "image" {
      return IMUIImageMessageContentView()
    }
    
    if type == "voice" {
      return VoiceWaveContentView()
    }
    
    if type == "video" {
      return IMUIVideoMessageContentView()
    }
    
    return IMUIBlackContentView()
  }
  
  override public var bubbleContentType: String {
    return type
  }
  
  override public var avatarFrame: CGRect {
    var rect = super.avatarFrame
    
    if isContinuous {
      rect.size.height = 0 // TO hiden continuous's message header
      return rect
    } else { return rect }
  }
  
  override public var bubbleFrame: CGRect {
    var rect = super.bubbleFrame
    if isContinuous {
      rect.origin.y = 0
      return rect
    } else { return rect }
  }
  
  override public var cellHeight: CGFloat {
    var normalHeight = super.cellHeight
    if isContinuous {
      return normalHeight - 40
    } else { return normalHeight }
  }
  
}

class IMUIBlackContentView: UIView, IMUIMessageContentViewProtocol{
  
  func layoutContentView(message message: IMUIMessageModelProtocol) { }
  
  func Activity() { }
  
  func inActivity () { }
}
