//
//  VoiceWaveContentView.swift
//  Themes
//
//  Created by oshumini on 2017/12/26.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AuroraIMUI

public class VoiceWaveContentView: UIView, IMUIMessageContentViewProtocol {
  open static var outGoingVoiceDurationColor = UIColor(netHex: 0x7587A8)
  open static var inComingVoiceDurationColor = UIColor(netHex: 0xFFFFFF)
  
  var voiceWaveLayer: CAShapeLayer = {
    var waveLayer = CAShapeLayer()
    waveLayer.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    waveLayer.lineWidth = 3
    
    return waveLayer
  }()
  
  var voiceWaveContent:[Float] = [0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1]
  
  fileprivate var isMediaActivity = false
  var message: IMUIMessageModelProtocol?
  var voiceDuration = UILabel()
  
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    self.layer.addSublayer(voiceWaveLayer)
    self.addSubview(voiceDuration)
    voiceDuration.textColor = UIColor.white
    voiceDuration.font = UIFont.systemFont(ofSize: 10.0)
    voiceDuration.frame = CGRect(origin: CGPoint.zero, size: CGSize(width: 40, height: 20))
    let gesture = UITapGestureRecognizer(target: self, action: #selector(self.onTapContentView))
    self.isUserInteractionEnabled = true
    self.addGestureRecognizer(gesture)
  }
  
  required public init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  public func layoutContentView(message: IMUIMessageModelProtocol) {
    self.message = message
    self.resetVoiceWaveLayer()
    let seconds = Int(message.duration)
    if seconds/3600 > 0 {
      voiceDuration.text = "\(seconds/3600):\(String(format: "%02d", (seconds/3600)%60)):\(seconds%60)"
    } else {
      voiceDuration.text = "\(seconds / 60):\(String(format: "%02d", seconds % 60))"
    }
    
    self.layoutToVoice(isOutGoing: message.isOutGoing)
    
    IMUIAudioPlayerHelper.sharedInstance.renewProgressCallback(message.msgId) { (id,averPower,currendTime, duration) in
      if self.message?.msgId == id {
        
        let power = pow(10, 0.1 * averPower);
        self.updateWave(with: power < 0.2 ? 0.2 : power)
      }
    }
  }
  
  @objc func onTapContentView() {
    if self.isMediaActivity {
      self.isMediaActivity = false
      IMUIAudioPlayerHelper.sharedInstance.stopAudio()
      self.resetVoiceWaveLayer()
    } else {
      
      do {
        let voiceData = try Data(contentsOf: URL(fileURLWithPath: (message?.mediaFilePath())!))
        IMUIAudioPlayerHelper
          .sharedInstance
          .playAudioWithData((self.message?.msgId)!,voiceData,
                             { (id, averPower,currendTime, duration) in
                              if self.message?.msgId == id {
                                let power = pow(10, 0.05 * averPower);
                                print("averPower:\(averPower)   power:\(power)")
                                self.updateWave(with: power < 0.1 ? 0.1 : power)
                              }
          },
                             { id in
                              if self.message?.msgId == id {
                                self.isMediaActivity = false
                                self.resetVoiceWaveLayer()
                              }
          },
                             {id in
                              if self.message?.msgId == id {
                                self.isMediaActivity = false
                                self.resetVoiceWaveLayer()
                              }
                            })
      } catch {
        print("load voice file fail")
      }
      
      self.isMediaActivity = true
    }
  }
  
  func updateWave(with power: Float) {
    self.voiceWaveContent.removeFirst()
    self.voiceWaveContent.append(power)
    self.voiceWaveLayer.path = VoiceWavePathFactory.createWavePath(self.voiceWaveContent).cgPath
  }
  
  func resetVoiceWaveLayer() {
    self.voiceWaveContent = self.voiceWaveContent.map { (power) -> Float in
      return 0.1
    }
    self.voiceWaveLayer.path = VoiceWavePathFactory.createWavePath(self.voiceWaveContent).cgPath
  }
  
  func layoutToVoice(isOutGoing: Bool) {
    if isOutGoing {
      self.voiceWaveLayer.frame = CGRect(x: frame.width - 92, y: 4, width: 72, height: 30)
      self.voiceWaveLayer.strokeColor = VoiceWaveContentView.outGoingVoiceDurationColor.cgColor
      self.voiceDuration.center = CGPoint(x: 30, y: frame.height/2)
      voiceDuration.textAlignment = .left
      voiceDuration.textColor = IMUIVoiceMessageContentView.outGoingVoiceDurationColor
    } else {
      self.voiceWaveLayer.frame = CGRect(x: 10, y: 4, width: 72, height: 30)
      self.voiceWaveLayer.strokeColor = VoiceWaveContentView.inComingVoiceDurationColor.cgColor
      self.voiceDuration.center = CGPoint(x: frame.width - 30, y: frame.height/2)
      voiceDuration.textAlignment = .right
      voiceDuration.textColor = IMUIVoiceMessageContentView.inComingVoiceDurationColor
    }
  }
}

