// pages/showdata/showdata.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
      inputData : {
        type:String,
        value:'default string'
      }
  },

  /**
   * 组件的初始数据
   */
  data: {

  },
  pageLifetimes: {
    show: function() {
      // 页面被展示
      console.log("showed")
    },
    hide: function() {
      // 页面被隐藏
    },
    resize: function(size) {
      // 页面尺寸变化
    }
  },
  /**
   * 组件的方法列表
   */
  methods: {
    onCreated:function(){
      console.log("created")
    },
  }
})
