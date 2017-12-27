//
//  DefaultMessageModel.swift
//  IMUIChat
//
//  Created by oshumini on 2017/3/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AuroraIMUI

class DefaultMessageModel: IMUIMessageModel {
  open var myTextMessage: String = ""
  
  var mediaPath: String = ""
  
  override func mediaFilePath() -> String {
    return mediaPath
  }

  
  override var resizableBubbleImage: UIImage {
    // return defoult message bubble
    return super.resizableBubbleImage
  }
  
  init(msgId: String, messageStatus: IMUIMessageStatus, fromUser: MyUser, isOutGoing: Bool, date: Date, type: String, text: String, mediaPath: String, layout: IMUIMessageCellLayoutProtocol, duration: CGFloat?) {
    
    self.myTextMessage = text
    self.mediaPath = mediaPath
    
    super.init(msgId: msgId, messageStatus: messageStatus, fromUser: fromUser, isOutGoing: isOutGoing, time: "", type: type, cellLayout: layout, duration: duration)
  }
  
  convenience init(text: String, isOutGoing: Bool) {
    
    let myLayout = DefaultMessageCellLayout(isOutGoingMessage: isOutGoing,
                                       isNeedShowTime: false,
                                       bubbleContentSize: DefaultMessageModel.calculateTextContentSize(text: text), bubbleContentInsets: UIEdgeInsets.zero, type: "text")
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId, messageStatus: .failed, fromUser: MyUser(), isOutGoing: isOutGoing, date: Date(), type: "text", text: text, mediaPath: "", layout:  myLayout, duration: nil)
  }
  
  convenience init(voicePath: String, duration: CGFloat, isOutGoing: Bool) {
    let myLayout = DefaultMessageCellLayout(isOutGoingMessage: isOutGoing,
                                       isNeedShowTime: false,
                                       bubbleContentSize: CGSize(width: 80, height: 37), bubbleContentInsets: UIEdgeInsets.zero, type: "voice")
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId, messageStatus: .sending, fromUser: MyUser(), isOutGoing: isOutGoing, date: Date(), type: "voice", text: "", mediaPath: voicePath, layout:  myLayout, duration: duration)
  }
  
  convenience init(imagePath: String, isOutGoing: Bool) {
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    
    var imgSize = CGSize(width: 120, height: 160)
    if let img = UIImage(contentsOfFile: imagePath) {
      imgSize = DefaultMessageModel.converImageSize(with: CGSize(width: (img.cgImage?.width)!, height: (img.cgImage?.height)!))
    }
    
    let myLayout = DefaultMessageCellLayout(isOutGoingMessage: isOutGoing,
                                       isNeedShowTime: false,
                                       bubbleContentSize: imgSize, bubbleContentInsets: UIEdgeInsets.zero, type: "image")
    self.init(msgId: msgId, messageStatus: .sending, fromUser: MyUser(), isOutGoing: isOutGoing, date: Date(), type: "image", text: "", mediaPath: imagePath, layout:  myLayout, duration: nil)
  }
  
  convenience init(videoPath: String, isOutGoing: Bool) {
    let myLayout = DefaultMessageCellLayout(isOutGoingMessage: isOutGoing,
                                       isNeedShowTime: false,
                                       bubbleContentSize: CGSize(width: 120, height: 160), bubbleContentInsets: UIEdgeInsets.zero, type: "video")
    let msgId = "\(NSDate().timeIntervalSince1970 * 1000)"
    self.init(msgId: msgId, messageStatus: .sending, fromUser: MyUser(), isOutGoing: isOutGoing, date: Date(), type: "video", text: "", mediaPath: videoPath, layout:  myLayout, duration: nil)
  }
  
  override func text() -> String {
    return self.myTextMessage
  }
  
  static func calculateTextContentSize(text: String) -> CGSize {
    let textSize  = text.sizeWithConstrainedWidth(with: IMUIMessageCellLayout.bubbleMaxWidth, font: UIFont.systemFont(ofSize: 18))
    
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
class DefaultMessageCellLayout: IMUIMessageCellLayout {
  
  var type: String
  
  init(isOutGoingMessage: Bool, isNeedShowTime: Bool, bubbleContentSize: CGSize, bubbleContentInsets: UIEdgeInsets, type: String) {
    self.type = type
    super.init(isOutGoingMessage: isOutGoingMessage, isNeedShowTime: isNeedShowTime, bubbleContentSize: bubbleContentSize, bubbleContentInsets: UIEdgeInsets.zero)
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
      return IMUIVoiceMessageContentView()
    }
    
    if type == "video" {
      return IMUIVideoMessageContentView()
    }
    
    return IMUIDefaultContentView()
  }
  
  override public var bubbleContentType: String {
    return type
  }

}

class IMUIDefaultContentView: UIView, IMUIMessageContentViewProtocol{
  
  func layoutContentView(message message: IMUIMessageModelProtocol) {
    
  }
  
  func Activity() {
    
  }
  
  func inActivity () {
    
  }
}


