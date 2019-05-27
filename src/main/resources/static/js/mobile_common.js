<script src="/static/js/jquery.min.js"></script>
<script src="/static/js/bootstrap.min.js"></script>
<script src="/static/js/json2.min.js"></script>
<script>
$(function(){
        var code = getQueryString("code");
        console.log("mobile_common_code:" + code);
        var openid = getOpenid(code);
        var mobile = "";
        console.log("mobile_common_openid:" + openid);


        function getQueryString(name) {
                var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                var r = window.location.search.substr(1).match(reg);
                if (r != null) return unescape(r[2]);
                return null;
        }

        function getOpenid(code){
            var openid = "";
            $.ajax({
                url: "/fuwuhao/getOpenid/" + code,
                async: false,
                type: "GET",
                success: function(result){
                    if (result != "null") {
                        var resultObj = JSON.parse(result);
                        openid = resultObj.openid;
                        mobile = resultObj.mobile;
                    } else {
                        openid = "1";
                    }
                }
            });
            return openid;
        }
});
</script>