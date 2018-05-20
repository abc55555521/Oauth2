$(function () {
	var timer;
    var aTab = document.getElementsByClassName('sub_tab');
    var aMain = document.getElementsByClassName('main');
    var onoff = true;
    function setFooter() {
        var oFooter = document.getElementsByClassName("footer")[0];
        var st = window.innerHeight;
        var h = oFooter.offsetHeight;
        var h_top = oFooter.offsetTop;
        var dic = st - (h + h_top);
		dic = dic <= -65 ? -65 : dic;
        oFooter.style.marginTop = dic - 20 + "px";
    }
    setFooter();
    window.onresize = function () {
        clearTimeout(timer);
        timer = setTimeout(function () {
            setFooter();
        }, 100)
    }
})


