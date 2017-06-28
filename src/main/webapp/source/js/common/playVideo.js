/**
 * Created by admin on 2016/8/23.
 */
function playVideo(url){
    //$("#videoCaputure").attr("src",url);
    $("#videoCaputure").attr("src",url);
   /* $("#videoCaputure").removeAttr("style");
    $("#videoCaputure").attr("style","display: block;height: 90.5%;");*/
    var wz = getDialogPosition($('#playWindow').get(0),150);
    $('#playWindow').window('open');
}
/*****************************************/
//视频播放
function vidplay() {
    var video = document.getElementById("playVideo");
    var button = document.getElementById("play");
    if (video.paused) {
        video.play();
        button.textContent = "||";
    } else {
        video.pause();
        button.textContent = ">";
    }
}

function restart() {
    var video = document.getElementById("playVideo");
    video.currentTime = 0;
}

function skip(value) {
    var video = document.getElementById("playVideo");
    video.currentTime += value;
}
/*****************************************/
