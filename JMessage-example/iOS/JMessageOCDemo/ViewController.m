//
//  ViewController.m
//  JMessage-AuroraIMUI-OC-Demo
//
//  Created by oshumini on 2017/6/5.
//  Copyright © 2017年 HXHG. All rights reserved.
//

#import "ViewController.h"
#import "ConversationViewController.h"
#import <JMessage/JMessage.h>

#define kuserName @"userName"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *userNameTF;
@property (weak, nonatomic) IBOutlet UITextField *passwordTF;

@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  if ([[NSUserDefaults standardUserDefaults] objectForKey:kuserName]) {
    ConversationViewController *conversationVC = [[ConversationViewController alloc] init];
    
    dispatch_async(dispatch_get_main_queue(), ^{
      [self getSingleConversation];
    });
    
    
  }
  
}

- (IBAction)onClickLogin:(id)sender {
  [_passwordTF resignFirstResponder];
  [_userNameTF resignFirstResponder];
  
  NSString *username = _userNameTF.text;
  NSString *password = _passwordTF.text;
  
  if ([self checkValidUsername:username AndPassword:password]) {
    [self loginUser:username Password:password];
  }
}

- (IBAction)clickToRegister:(id)sender {
  [_passwordTF resignFirstResponder];
  [_userNameTF resignFirstResponder];
  
  NSString *username = _userNameTF.text;
  NSString *password = _passwordTF.text;
  
  if ([self checkValidUsername:username AndPassword:password]) {
    [JMSGUser registerWithUsername:username password:password completionHandler:^(id resultObject, NSError *error) {
      if(error == nil) {
        [self loginUser:username Password:password];
      }
    }];
    
  }
}

- (void)loginUser:(NSString *)username Password:(NSString *)password {
  
  
  if ([self checkValidUsername:username AndPassword:password]) {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"正在登陆" message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:nil, nil];
    [alert show];
    [JMSGUser loginWithUsername:username password:password completionHandler:^(id resultObject, NSError *error) {
      [alert dismissWithClickedButtonIndex:0 animated:NO];
      if (error == nil) {
        [[NSUserDefaults standardUserDefaults] setObject:username forKey:kuserName];
        [self getSingleConversation];
      }
    }];
  }
  
}

- (void)getSingleConversation {
  JMSGConversation *conversation = [JMSGConversation singleConversationWithUsername:@"5558"];
  if (conversation == nil) {
    
    [JMSGConversation createSingleConversationWithUsername:@"5558" completionHandler:^(id resultObject, NSError *error) {
      if (error) {
        NSLog(@"创建会话失败");
        return ;
      }
      
      ConversationViewController *conversationVC = [[ConversationViewController alloc] init];
      conversationVC.conversation = resultObject;
      [self presentViewController:conversationVC animated:true completion:^{}];
    }];
  } else {
    ConversationViewController *conversationVC = [[ConversationViewController alloc] init];
    conversationVC.conversation = conversation;
    [self presentViewController:conversationVC animated:true completion:^{}];
  }
}

- (BOOL)checkValidUsername:username AndPassword:password {
  if (![password isEqualToString:@""] && ![username isEqualToString:@""]) {
    return YES;
  }
  
  NSString *alert = @"用户名或者密码不合法.";
  if ([username isEqualToString:@""]) {
    alert =  @"用户名不能为空";
  } else if ([password isEqualToString:@""]) {
    alert = @"密码不能为空";
  }
  
  return NO;
}

- (void)showToast:(NSString *)alert {
  
  //  [alertView dismissWithClickedButtonIndex:0 animated:YES];
}

- (void)didReceiveMemoryWarning {
  [super didReceiveMemoryWarning];
  
}


@end
