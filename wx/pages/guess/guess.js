// pages/guess/guess.js
let timer;
let timerSelf
let flashCount
Page({

  /**
   * 页面的初始数据
   */
  data: {
      winCount : 0,
      imageAISrc:'/images/ai.png',
      imageSelfSrc:'/images/self.png',
      guessIcons: [ "/images/jiandao.png",'/images/shitou.png',"/images/bu.png"],
      winOrLose:'VS',
      selectIndex:-1,
      aiSelectIndex:-1,
      maskState:false,
      randAi:0,
      randSelf:0,
      gameState:0,
      buttonText:'出拳'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      winCount: 0,
      selectIndex: -1
    })
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  selectIcon: function(event) {
    if(this.CanOption()){
      this.setData({
        selectIndex: event.currentTarget.id
      })
      console.log(this.data.selectIndex)
  
    }
   
  },
  CanOption:function(){
    return !this.data.maskState
  },
 
  comfireTurn:function(){
    if(this.CanOption()){
      if(this.data.gameState == 0){
        if(this.data.selectIndex == -1){
          this.showErrorMsg("没有选择")
        }else{
          const aiSelect = Math.floor(Math.random() * 3)
          this.setData({
            maskState:true,
            aiSelectIndex:aiSelect,
            gameState: 1
          })
          flashCount = 0
          timer = setInterval(this.turnRun,Math.floor(Math.random()*20 + 80))
          timerSelf = setInterval(this.turnRunSelf,Math.floor(Math.random()*10 + 90))
          this.flashIcon()
        }
      
      }else{
        this.resetState()
      }
     
    }
  },
  turnRun:function(){
    if(flashCount > 10){
      clearInterval(timer)
      clearInterval(timerSelf)
      this.showResult()
    }else{
      this.flashIcon()
    }
   
  },
  turnRunSelf:function(){
      this.flashIconSelf()
  },
  flashIcon:function() {
    let index = this.data.randAi 
    index++
    if(index >=3){
      index = 0
    }
    
    this.setData({
      imageAISrc:this.data.guessIcons[index],
      randAi:index
    })
    flashCount++
  },
  flashIconSelf:function() {
    let index = this.data.randSelf 
    index++
    if(index >=3){
      index = 0
    }
    
    this.setData({
      imageSelfSrc:this.data.guessIcons[index],
      randSelf:index
    })

  },
  showResult:function(){
    let res
    let count = this.data.winCount
    if(this.data.aiSelectIndex == this.data.selectIndex){
      res = "平局"
    }else{
      if(this.data.aiSelectIndex == this.data.selectIndex + 1
        || (this.data.aiSelectIndex == this.data.selectIndex  - 2)
        ){
        res = "失败"
      }else{
        res = "胜利"
        count++
      }
    }
    this.setData({
      imageSelfSrc:this.data.guessIcons[this.data.selectIndex],
      imageAISrc:this.data.guessIcons[this.data.aiSelectIndex],
      maskState:false,
      buttonText:"再来",
      winOrLose:res,
      winCount:count,
      
    })
  },
  resetState:function(){
    this.setData({
      gameState: 0,
      buttonText:"出拳",
      winOrLose:'vs',
      imageAISrc:'/images/ai.png',
      imageSelfSrc:'/images/self.png',
      selectIndex:-1
    })
  },
  showErrorMsg:function(msg){
    wx.showToast({
      title:msg,
      image: '/images/cus.png',
    })
  }
})