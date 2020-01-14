// pages/stock/stock.js

let Charts = require("../../utils/wxcharts.js")
const echarts = require('../../ec-canvas/echarts')//路径一定要正确否则会调用错误
Page({
  /**
   * 页面的初始数据
   */
  chart4Profit:null,
  chart4bar:null,
  data: {
    hostUrl : 'https://stock.how2game.club',
    // hostUrl : 'https://127.0.0.1:8031/',
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
    indexStartDate:'',
    indexEndDate:'',
    freshData:true,

    //返回的数据
    indexDatas:[],
    dates:[],
    closePoints:[],

    profits:[],
    profitValues:[],
   
    trades:[],

//trade
    winCount: 0,
    lossCount: 0,
    avgWinRate:0,
    avgLossRate:0,

    years:0,
    indexIncomeTotal:0,
    indexIncomeAnnual:0,
    trendIncomeTotal:0,
    trendIncomeAnnual:0,
    
    
//收益分布
    annualProfits:[],
    annuals:[],
    indexIncomes:[],
    trendIncomes:[],

    //echart
    ecData:  {
      lazyLoad: true 
      // onInit: initChart
    },
    ecData2:  {
      lazyLoad: true 
      // onInit: initChart
    }
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
      selectIndex: e.detail,
      freshData:true
    })
    this.getCodeData()
  },

  bindMultiPickerChange: function (e) {
    console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      multiIndex: e.detail.value,
      freshData:false
    })
    this.getCodeData()
  },
  bindDateChangeS:function(e){
    this.setData({
      startDate: e.detail.value,
      freshData:false
    })
    this.getCodeData()
  },
  bindDateChangeE:function(e){
    console.log(e)
    this.setData({
      endDate: e.detail.value,
      freshData:false
    })
    this.getCodeData()
  },
  getCodes:function(){
    var url = this.data.hostUrl + "/codes"
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
    var url = this.data.hostUrl + "/simulate/"
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
    let profits =res.data.profits
    let dates = []
    let closePoints = []
    let profitValues = []

    let trades = res.data.trades
    indexDatas.forEach(item => {
      dates.push(item.date)
      closePoints.push(item.closePoint)
    })

    
  
    profits.forEach(item => {
      profitValues.push(item.value)
    })


    let years = res.data.years;
    let indexIncomeTotal = res.data.indexIncomeTotal;
    let indexIncomeAnnual = res.data.indexIncomeAnnual;
    let trendIncomeTotal = res.data.trendIncomeTotal;
    let trendIncomeAnnual = res.data.trendIncomeAnnual;

    let winCount = res.data.winCount;
    let lossCount = res.data.lossCount;
    let avgWinRate = res.data.avgWinRate;
    let avgLossRate = res.data.avgLossRate;
   
    let annualProfits = res.data.annualProfits
    let annuals = []
    let indexIncomes = []
    let trendIncomes = []
    annualProfits.forEach(item => {
      annuals.push(item.year)
      indexIncomes.push(item.indexIncome)
      trendIncomes.push(item.trendIncome)
    })


    let option = {
      trades:trades,
      annualProfits:annualProfits,
      annuals:annuals,
      indexIncomes:indexIncomes,
      trendIncomes:trendIncomes,
       indexDatas : indexDatas,
      dates : dates,
      closePoints :closePoints,
      profits:profits,
      profitValues:profitValues,
      years:years,
      indexIncomeTotal:indexIncomeTotal,
      indexIncomeAnnual:indexIncomeAnnual,
      trendIncomeTotal:trendIncomeTotal,
      trendIncomeAnnual:trendIncomeAnnual,
      avgLossRate:avgLossRate,
      avgWinRate:avgWinRate,
      lossCount:lossCount,
      winCount:winCount
    }
    if(this.data.freshData){
       //date
      let indexStartDate = res.data.indexStartDate;
      let indexEndDate = res.data.indexEndDate;

      option.indexStartDate = indexStartDate,
      option.indexEndDate = indexEndDate,
      option.startDate = indexStartDate,
      option.endDate = indexEndDate

    }
    this.setData(option)
    if(this.chart4Profit != null){
      this.updateChartData()
    }else{
      this.initChart()
    }
    if(this.chart4bar != null){
      this.updateChartBarData()
    }else{
      this.initBarChart()
    }
  },
  reSetData:function(){
    this.setData({

    })
  },
 
  updateChartData:function(){ 
    let option = this.getOption();
    this.chart4Profit.setOption(option)
  },

  initChart:function(){
    this.ecComponent.init((canvas, width, height) => {
      const chart = echarts.init(canvas, null, {
        width: width,
        height: height
      });
      canvas.setChart(chart);
    
      // 指定图表的配置项和数据
      var option =  this.getOption()
      chart.setOption(option);
      this.chart4Profit = chart
      return chart;
    })
  },
  onReady: function () {
    // 获取组件
    this.ecComponent = this.selectComponent('#chart1');
    this.ecBar = this.selectComponent('#chart2');
  },
  getOption:function(){
    var option = {
      title: {
        text: "投资指数",
        left: 'center'
      },
      color: ["#ff0000", "#67E0E3"],
      legend: {
        data: [this.data.selectArray[this.data.selectIndex].name,"趋势投资"],
        top: 50,
        left: 'center',
        z: 100
      },
      grid: {
        containLabel: true
      },
      tooltip: {
        show: true,
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: this.data.dates,
        // show: false
      },
      yAxis: {
        x: 'center',
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
        // show: false
      },
      dataZoom:[
        {
        　　　　type: 'slider',//图表下方的伸缩条
        　　　　show : true, //是否显示
        　　　　realtime : true, //拖动时，是否实时更新系列的视图
        　　　　start : 90, //伸缩条开始位置（1-100），可以随时更改
        　　　　end : 100, //伸缩条结束位置（1-100），可以随时更改
      　　　}
      ],
      series: [
        {
        name: this.data.selectArray[this.data.selectIndex].name,
        type: 'line',
        smooth: true,
        data: this.data.closePoints
      },
      {
        name: "趋势投资",
        type: 'line',
        smooth: true,
        data: this.data.profitValues
      }
    
    ]
    };
    return option;
  },

  updateChartBarData:function(){ 
    let option = this.getBarOption();
    this.chart4bar.setOption(option)
  },

  initBarChart:function(){
    this.ecBar.init((canvas, width, height) => {
      const chart = echarts.init(canvas, null, {
        width: width,
        height: height
      });
      canvas.setChart(chart);
    
      // 指定图表的配置项和数据
      var option =  this.getBarOption()
      chart.setOption(option);
      this.chart4bar = chart
      return chart;
    })
  },
  getBarOption:function(){
    var option = {
      title: {
        text: "投资指数",
        left: 'center'
      },
      color: ["#ff0000", "#67E0E3"],
      legend: {
        data: [this.data.selectArray[this.data.selectIndex].name,"趋势投资"],
        top: 50,
        left: 'center',
        z: 100
      },
      grid: {
        left: 20,
        right: 20,
        bottom: 15,
        top: 40,
        containLabel: true
      },
      tooltip: {
        show: true,
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        axisLine: {
          lineStyle: {
            color: '#999'
          }
        },
        axisLabel: {
          color: '#666'
        },
        boundaryGap: false,
        data: this.data.annuals,
        // show: false
      },
      yAxis: {
        offset:23,
        x: 'center',
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
        // show: false
      },
      
      series: [
        {
        name: this.data.selectArray[this.data.selectIndex].name,
        type: 'bar',
        smooth: true,
        data: this.data.indexIncomes
      },
      {
        name: "趋势投资",
        type: 'bar',
        smooth: true,
        data: this.data.trendIncomes
      }
    
    ]
    };
    return option;
  },
})

 