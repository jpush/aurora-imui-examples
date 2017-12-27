//
//  VoiceWavePathFactory.swift
//  Themes
//
//  Created by oshumini on 2017/12/26.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class VoiceWavePathFactory: NSObject {
  static var levelWidth = 6
  static var levelMargin = 1
  
  class func createWavePath(_ levelArr:[Float]) -> UIBezierPath {

    let wavePath = UIBezierPath()
    for (index, level) in levelArr.enumerated() {
      let x = index * (VoiceWavePathFactory.levelWidth + VoiceWavePathFactory.levelMargin) + 2
      let centerY = 20
      let levelHight = level * 40
      let startY: Int = centerY - Int(levelHight/2)
      let endY: Int = 20 + Int(levelHight/2)
      
      wavePath.move(to: CGPoint(x: x, y: startY))
      wavePath.addLine(to: CGPoint(x: x, y: Int(endY)))
    }
    
    return wavePath
  }
}
