// components/select/select.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    propArray : {
      type:Array
    }

  },

  /**
   * 组件的初始数据
   */
  data: {
    nowText:'',
    animationData:{},
    selectShow:false
  },
  lifetimes : {
    ready:function(){
      if (this.properties.propArray.length >0 ){
        this.setData({
          nowText:this.properties.propArray[0].text
        })
      }
     
    }
  },
  /**
   * 组件的方法列表
   */
  methods: {
    initArrary:function(){
      if (this.properties.propArray.length >0 ){
        this.setData({
          nowText:this.properties.propArray[0].text
        })
      }
    },
    selectToggle : function(e){
      // console.log(this.properties.propArray)
      let nowShow = this.data.selectShow
      let animation = wx.createAnimation({
        delay: 0,
        timingFunction:"ease"
      })
      this.animation = animation

      if(nowShow){
        animation.rotate(0).step()
      }else{
        animation.rotate(180).step()
      }
      this.setData({
        animationData: animation.export(),
        selectShow: !nowShow
      })
    },
    setText :function(e){
      var nowData = this.properties.propArray
      var nowId = e.currentTarget.dataset.id
      console.log(e) 
      var nowText = nowData[nowId].text
      this.animation.rotate(0).step()
      this.setData({
        selectShow : false,
        animationData: this.animation.export(),
        nowText:nowText
      })
      this.triggerEvent('changeSelectIndex',nowId)
    }
  }
})
