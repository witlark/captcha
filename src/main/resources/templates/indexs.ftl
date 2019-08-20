<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

    <script type="text/javascript" src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/layer.js"></script>

    <style>
        .rightValidate { width: 280px; margin: 0px auto; position: relative; line-height: 30px; height: 30px; text-align: center; z-index: 99; margin-top:60px;}
        .v_rightBtn { position: absolute; left: 0; top: 0; height: 33px; width: 40px; background: #ddd; cursor: pointer; }
        .imgBtn{ width:44px; height: 171px; position: absolute; left: 0;  }
        .imgBtn img{ z-index:99; align:center;}
        .imgBg{ position:absolute;bottom:35px;width: 280px; height: 171px;  display:none;z-index:9;}
        .imgBg_2{ position:absolute; bottom: -80px;  bottom:35px; width: 150px; height: 60px;   display:block;z-index:9;}
        .imgBg_3{ position:absolute;  bottom: -80px; bottom:35px; width: 170px; height: 60px;   display:block;z-index:9;}
        .hkinnerWrap{ border: 1px solid #eee; z-index:9999}
        .green{ border-color:#34C6C2 !important; }
        .green .v_rightBtn{ background: #34C6C2; color: #fff; }
        .red{ border-color:red !important; }
        .red .v_rightBtn{ background: red; color: #fff; }
        .refresh{ position: absolute; width: 30px; height: 30px; right: 4px; top: 4px; font-size: 12px; color: #fff; text-shadow: 0px 0px 9px #333; cursor: pointer; display: none; }
        .notSel{ user-select: none; -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; -webkit-touch-callout: none; }
    </style>



</head>
<body>


<div style = "width: 100%; height:250px;"></div>

<div class="comImageValidate rightValidate">

    <div class="imgBg_2"></div>
    <div class="imgBg_3"></div>
    <div style = "width: 100%; height: 30px;"></div>
    <div class="imgBg">
        <div class="imgBtn">
            <img alt="" src="">
        </div>
        <span class="refresh" >
	        	<img alt="" src="">
	        </span>
    </div>

    <div class="hkinnerWrap" style="height:30px; position: relative">
        <span  class="v_rightBtn "><em class="notSel">→</em></span>
        <span class="huakuai"  style="font-size: 12px;line-height: 33px;color: #A9A9A9;">向右滑动滑块填充拼图完成验证</span>
        <input type = "hidden" name="validX"/>
    </div>

</div>





<script>
    var tokenId = "";
    var y = "";
    var x = "";
    $(".comImageValidate").ready(function () {
        validateImageInit();
        $(".refresh").click(function () {
            validateImageInit();
        })
        $(".hkinnerWrap").mouseover(function(){
            $(".imgBg").css("display","block");
            $(".refresh").css("display","block");
        }).mouseleave(function () {
            $(".imgBg").css("display","none");
            $(".refresh").css("display","none");
        });

        $(".imgBg").mouseover(function(){
            $(".imgBg").css("display","block");
            $(".refresh").css("display","block");
        }).mouseleave(function () {
            $(".imgBg").css("display","none");
            $(".refresh").css("display","none");
        });

        $('.v_rightBtn').on({
            mousedown: function(e) {
                $(".huakuai").html("");
                $(".hkinnerWrap").removeClass("red green")
                var el = $(this);
                var os = el.offset();
                dx = e.pageX - os.left;
                //$(document)
                $(this).parents(".hkinnerWrap").off('mousemove');
                $(this).parents(".hkinnerWrap").on('mousemove', function(e) {
                    var newLeft=e.pageX - dx;
                    el.offset({
                        left: newLeft
                    });
                    var newL=parseInt($(".v_rightBtn").css("left"));
                    if(newL<=0){
                        newL=0;
                    }else if (newL>=298){
                        newL=306;
                    }
                    $(".v_rightBtn").css("left",newL+"px");
                    $(".imgBtn").offset({
                        left: newLeft
                    });
                    $(".imgBtn").css("left",newL+"px")
                }).on('mouseup', function(e) {
                    //$(document)
                    $(this).off('mousemove');
                })
            }
        }).on("mouseup",function () {
            $(this).parents(".hkinnerWrap").off('mousemove');
            var l=$(this).css("left");
            if(l.indexOf("px")!=-1){
                l=l.substring(0,l.length-2);
            }
            x = l;


            submitDate(l,y,tokenId)
        })

    });
    /*图形验证*/
    function submitDate(x,y,tokenId) {

        $.ajax({
            url:"/captcha/check/verification/result?X="+x+"&Y="+y,
            dataType:'json',
            type: "POST",
            success:function (data) {
                if(data==true){
                    $(".hkinnerWrap").addClass("green").removeClass("red");
                    $(".hkinnerWrap input[name='validX']").val(x);
                    $("#X").val(x);
                    $("#Y").val(y);
                    layer.msg("验证成功", {time:1000,icon:1})
                } else {
                    $(".hkinnerWrap").addClass("red").removeClass("green");
                    setTimeout(function(){
                        $(".hkinnerWrap").removeClass("red green");
                        $(".v_rightBtn").css("left",0);
                        $(".imgBtn").css("left",0);
                    },280)
                    validateImageInit();
                }
            }
        })
    }

    var forumId=GetQueryString("type" );
    //url参数
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)" + name +"=([^&]*)(&|$)" );
        var r = window.location.search.substr(1).match(reg);

        if(r!=null )
            return  unescape(r[2]);
        return null;

    }


    /*获取验证码*/
    function validateImageInit() {
        var url = "/captcha/get/verification/image";
        var parm = GetQueryString("type");
        if (parm != null) {
            url += "?type=" + parm;
        }
        console.log(url);
        $.ajax({
            url: url,
            dataType:'json',
            cache:false,
            type: "get",
            success:function (data) {
                var type = data.type;
                console.log(type);
                switch (type) {
                    case "operation" : initOperationVerificationCode(data); break;
                    case "char" : initCharVerificationCode(data); break;
                    case "slide" : initSlideVerificationCode(data); break;
                    default : console.log("验证码错误"); break;
                }
            },error:function(err){
                validateImageInit();
            }
        })
    }

    function initOperationVerificationCode(data) {
        $(".imgBg_3").css("background",'#fff url("data:image/jpg;base64,'+data.operationImage+'")');
    }

    function initSlideVerificationCode(data) {
        y = data.y;
        $(".huakuai").html("向右滑动滑块填充拼图");
        $(".imgBg").css("background",'#fff url("data:image/jpg;base64,'+data.shadeImage+'")');
        $(".imgBtn").css('top', y+ "px");
        $(".imgBtn").find("img").attr("src","data:image/png;base64,"+data.cutoutImage)
        $(".hkinnerWrap").removeClass("red green");
        $(".v_rightBtn").css("left",0);
        $(".imgBtn").css("left",0);
    }

    function initCharVerificationCode(data) {
        $(".imgBg_2").css("background",'#fff url("data:image/jpg;base64,'+data.charImage+'")');
        $(".imgBg_3").css("none");
    }

</script>

</body>
</html>