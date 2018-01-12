//
//  MinimalChatViewController.swift
//  Themes
//
//  Created by oshumini on 2017/12/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//
import UIKit
import Photos
import AuroraIMUI

class MinimalChatViewController: UIViewController {
  
  @IBOutlet weak var messageList: IMUIMessageCollectionView!
  @IBOutlet weak var chatInputView: IMUIInputView!
  
  let imageManage: PHCachingImageManager = PHCachingImageManager()
  
  override func viewDidLoad() {
    super.viewDidLoad()
    self.chatInputView.inputViewDelegate = self
    self.navigationController?.navigationBar.isTranslucent = false
    IMUIMessageCellLayout.bubbleOffsetToAvatar = UIOffset(horizontal: 8 , vertical: 0)
    IMUITextMessageContentView.inComingTextColor = UIColor(netHex: 0x222222)
    IMUITextMessageContentView.outGoingTextColor = UIColor(netHex: 0x222222)
    self.setThemeColor(UIColor(netHex: 0xF8FBFF))
    self.messageList.delegate = self
  }
  
  func setThemeColor(_ color: UIColor) {
    messageList.messageCollectionView.backgroundColor = color
    for view in chatInputView.subviews {
      view.backgroundColor = color
    }
    chatInputView.featureSelectorView.featureListCollectionView.backgroundColor = color
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }
}


// MARK: - IMUIInputViewDelegate
extension MinimalChatViewController: IMUIInputViewDelegate {
  
  func sendTextMessage(_ messageText: String) {
    let outGoingmessage = MinimalMessageModel(text: messageText, isOutGoing: true)
    let inCommingMessage = MinimalMessageModel(text: messageText, isOutGoing: false)
    self.messageList.appendMessage(with: outGoingmessage)
    self.messageList.appendMessage(with: inCommingMessage)
  }
  
  func switchIntoRecordingVoiceMode(recordVoiceBtn: UIButton) {
    
  }
  
  func didShootPicture(picture: Data) {
    let imgPath = self.getPath()
    
    do {
      try picture.write(to: URL(fileURLWithPath: imgPath))
      DispatchQueue.main.async {
        let outGoingmessage = MinimalMessageModel(imagePath: imgPath, isOutGoing: true)
        let inCommingMessage = MinimalMessageModel(imagePath: imgPath, isOutGoing: false)
        self.messageList.appendMessage(with: outGoingmessage)
        self.messageList.appendMessage(with: inCommingMessage)
      }
    } catch {
      print("write image file error")
    }
    
  }
  
  func finishRecordVideo(videoPath: String, durationTime: Double) {
    let outGoingmessage = MinimalMessageModel(videoPath: videoPath, isOutGoing: true)
    let inCommingMessage = MinimalMessageModel(videoPath: videoPath, isOutGoing: false)
    self.messageList.appendMessage(with: outGoingmessage)
    self.messageList.appendMessage(with: inCommingMessage)
  }
  
  func finishRecordVoice(_ voicePath: String, durationTime: Double) {
    
    let outGoingmessage = MinimalMessageModel(voicePath: voicePath, duration: CGFloat(durationTime), isOutGoing: true)
    let inCommingMessage = MinimalMessageModel(voicePath: voicePath, duration: CGFloat(durationTime), isOutGoing: false)
    self.messageList.appendMessage(with: outGoingmessage)
    self.messageList.appendMessage(with: inCommingMessage)
  }
  
  func didSeletedGallery(AssetArr: [PHAsset]) {
    for asset in AssetArr {
      switch asset.mediaType {
      case .image:
        let option = PHImageRequestOptions()
        option.isSynchronous = true
        
        imageManage.requestImage(for: asset, targetSize: CGSize(width: 100.0, height: 100.0), contentMode: .aspectFill, options: option, resultHandler: { [weak self] (image, _) in
          let imageData = UIImagePNGRepresentation(image!)
          self?.didShootPicture(picture: imageData!)
        })
        break
      default:
        break
      }
    }
  }
  
  func getPath() -> String {
    var recorderPath:String? = nil
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yy-MMMM-dd"
    recorderPath = "\(NSHomeDirectory())/Documents/"
    recorderPath?.append("\(NSDate.timeIntervalSinceReferenceDate)")
    return recorderPath!
  }
}


// MARK - IMUIMessagemessageListDelegate
extension MinimalChatViewController: IMUIMessageMessageCollectionViewDelegate {
  
  
  func messageList(_: UICollectionView, forItemAt: IndexPath, model: IMUIMessageModelProtocol) {
  }
  
  func messageList(didTapMessageBubbleInCell: UICollectionViewCell, model: IMUIMessageModelProtocol) {
    self.showToast(alert: "tap message bubble")
  }
  
  func messageList(didTapHeaderImageInCell: UICollectionViewCell, model: IMUIMessageModelProtocol) {
    self.showToast(alert: "tap header image")
  }
  
  func messageList(didTapStatusViewInCell: UICollectionViewCell, model: IMUIMessageModelProtocol) {
    self.showToast(alert: "tap status View")
  }
  
  func messageList(_: UICollectionView, willDisplayMessageCell: UICollectionViewCell, forItemAt: IndexPath, model: IMUIMessageModelProtocol) {
    
  }
  
  func messageList(_: UICollectionView, didEndDisplaying: UICollectionViewCell, forItemAt: IndexPath, model: IMUIMessageModelProtocol) {
    
  }
  
  func messageCollectionView(_ willBeginDragging: UICollectionView) {
    self.chatInputView.hideFeatureView()
  }
  
  func showToast(alert: String) {
    
    let toast = UIAlertView(title: alert, message: nil, delegate: nil, cancelButtonTitle: nil)
    toast.show()
    toast.dismiss(withClickedButtonIndex: 0, animated: true)
  }
}



