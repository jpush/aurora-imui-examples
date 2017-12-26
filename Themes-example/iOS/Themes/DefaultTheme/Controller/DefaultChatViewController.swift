//
//  DefaultChatViewController.swift
//  Themes
//
//  Created by oshumini on 2017/12/20.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import Photos
import AuroraIMUI

class DefaultChatViewController: UIViewController {
  
  @IBOutlet weak var messageList: IMUIMessageCollectionView!
  @IBOutlet weak var chatInputView: IMUIInputView!
  
  let imageManage: PHCachingImageManager = PHCachingImageManager()
  
  override func viewDidLoad() {
    super.viewDidLoad()
    self.chatInputView.inputViewDelegate = self
    self.messageList.delegate = self
  }
  
  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }
}


// MARK: - IMUIInputViewDelegate
extension DefaultChatViewController: IMUIInputViewDelegate {
  
  func sendTextMessage(_ messageText: String) {
    let outGoingmessage = DefaultMessageModel(text: messageText, isOutGoing: true)
    let inCommingMessage = DefaultMessageModel(text: messageText, isOutGoing: false)
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
        let outGoingmessage = DefaultMessageModel(imagePath: imgPath, isOutGoing: true)
        let inCommingMessage = DefaultMessageModel(imagePath: imgPath, isOutGoing: false)
        self.messageList.appendMessage(with: outGoingmessage)
        self.messageList.appendMessage(with: inCommingMessage)
      }
    } catch {
      print("write image file error")
    }
    
  }
  
  func finishRecordVideo(videoPath: String, durationTime: Double) {
    let outGoingmessage = DefaultMessageModel(videoPath: videoPath, isOutGoing: true)
    let inCommingMessage = DefaultMessageModel(videoPath: videoPath, isOutGoing: false)
    self.messageList.appendMessage(with: outGoingmessage)
    self.messageList.appendMessage(with: inCommingMessage)
  }
  
  func finishRecordVoice(_ voicePath: String, durationTime: Double) {
    
    let outGoingmessage = DefaultMessageModel(voicePath: voicePath, duration: CGFloat(durationTime), isOutGoing: true)
    let inCommingMessage = DefaultMessageModel(voicePath: voicePath, duration: CGFloat(durationTime), isOutGoing: false)
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
extension DefaultChatViewController: IMUIMessageMessageCollectionViewDelegate {
  
  
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
  
  func messageList(_ willBeginDragging: UICollectionView) {
    self.chatInputView.hideFeatureView()
  }
  
  func showToast(alert: String) {
    
    let toast = UIAlertView(title: alert, message: nil, delegate: nil, cancelButtonTitle: nil)
    toast.show()
    toast.dismiss(withClickedButtonIndex: 0, animated: true)
  }
}


