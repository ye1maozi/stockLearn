// pages/stock/stock.js

let Charts = require("../../utils/wxcharts.js")
let chart4Profit
Page({
  /**
   * 页面的初始数据
   */
  data: {
    hostUrl : 'https://127.0.0.1:8031/',
    //下拉
    selectArray:[],
    selectIndex:0,
    //picker
    mas : [],
    buyThreshold:[],
    sellThreshold:[],
    serviceCharge:[],
    multiArray: [],
    multiIndex: [0, 0, 0,0],
    //date
    startDate:'',
    endDate:'',

    //返回的数据
    indexDatas:[],
    dates:[],
    closePoints:[],

    profits:[],
   
    trades:[],

    
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let buyThreshold = []
    let sellThreshold = []
    let mas =  [{
      id:5,
      name:"5日"
    },
    {
      id:10,
      name:"10日"
    },
    {
      id:20,
      name:"20日"
    },
    {
      id:60,
      name:"60日"
    }
  ]
    let serviceCharge = [
      {
        id:0,
        name:"0.0"
      },
      {
        id:0.001,
        name:"0.1%"
      },
      {
        id:0.0015,
        name:"0.15%"
      },
      {
        id:0.002,
        name:"0.2%"
      },
    ]

    let multiArray = [mas,buyThreshold,sellThreshold,serviceCharge]
  
    for (var i=1;i<10;i++)
    { 
      let tmp = 1 + i/100
      buyThreshold.push( {
        id: tmp,
        name:tmp
      })
    }
  
    for (var i=1;i<10;i++)
    { 
      let tmp = 1 - i/100
      sellThreshold.push( {
        id: tmp,
        name:tmp
      })
    }
    this.setData({
      buyThreshold:buyThreshold,
      sellThreshold:sellThreshold,
      multiArray:multiArray,
      mas:mas,
      serviceCharge:serviceCharge
    })

    this.getCodes()
  },
  onChangeSelect:function(e) {
    console.log( e.detail)
    this.setData({
      selectIndex: e.detail
    })
    this.getCodeData()
  },

  bindMultiPickerChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      multiIndex: e.detail.value
    })
    this.getCodeData()
  },
  bindDateChangeS:function(e){
    this.setData({
      startDate: e.detail.value
    })
  },
  bindDateChangeE:function(e){
    console.log(e)
    this.setData({
      endDate: e.detail.value
    })
  },
  getCodes:function(){
    var url = this.data.hostUrl + "api-codes/codes"
    console.log(url)
    wx.request({
      url: url,
      success:this.onGetCodes
    })
  },
  onGetCodes:function(res){
    var selectArray = []
    res.data.forEach(item => {
      
      item.text = item.name
      selectArray.push(item)
    });
    this.setData({
      selectArray:selectArray
    })

    // console.log(this.data.selectArray)
    var showTwo = this.selectComponent('#selectBox');
    showTwo.initArrary()
    // this.createChart()
    this.getCodeData()
    
  },
  getCodeData:function(){
    var url = this.data.hostUrl + "api-backtest/simulate/"
    let code = this.data.selectArray[this.data.selectIndex].code
    let multiIndex = this.data.multiIndex
    let multiArray = this.data.multiArray
 
    // console.log(multiArray[2][multiIndex[2]])
    url += code +"/"
            + multiArray[0][multiIndex[0]].id + "/"
              + multiArray[1][multiIndex[1]].id + "/"
                + multiArray[2][multiIndex[2]].id + "/"
                  + multiArray[3][multiIndex[3]].id+ "/"
                    + (this.data.startDate !=''?this.data.startDate:"null") + "/"
                    + (this.data.endDate !=''?this.data.endDate:"null" )
   
     
    console.log(url)
    wx.request({
      url: url,
      success:this.onGetCodesData
    })
  },
  onGetCodesData:function(res){
    console.log(res.data)
    let indexDatas = res.data.indexDatas
    let dates = []
    let closePoints = []
    indexDatas.forEach(item => {
      dates.push(item.date)
      closePoints.push(item.closePoint)
    })

    this.setData({
      indexDatas : indexDatas,
      dates : dates,
      closePoints :closePoints
    })
  

    if(chart4Profit == null){
      this.createChart()
    }else{
      chart4Profit.updateData({
        categories: this.data.dates,
        series: [{
            name: this.data.selectArray[this.data.selectIndex].name,
            data: this.data.closePoints,
        }], 
      })
    }
    ;
  },
  reSetData:function(){
    this.setData({

    })
  },

  createChart:function(){

    let windowWidth = 320;
    try {
        let res = wx.getSystemInfoSync();
        windowWidth = res.windowWidth;
    } catch (e) {
        // do something when get system info failed
    }
    chart4Profit = new Charts({
        canvasId: 'lineCanvas',
        type: 'line',
        categories: this.data.dates,
        series: [{
            name: this.data.selectArray[this.data.selectIndex].name,
            data: this.data.closePoints,
        }],
        
        width: windowWidth,
        height: 400,

        dataPointShape:false,
        dataLabel:false,
        // enableScroll:true,
        title:{
          name:this.data.selectArray[this.data.selectIndex].name
        }
    });
    
  },
})