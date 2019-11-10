$().ready(function () {
    (function ($) {
        $.getUrlParam = function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return decodeURIComponent(r[2]);
            return null;
        }
    })(jQuery);

    var isSuccess = $.getUrlParam("success");
    if (isSuccess === "true") {
        $("#pageSuccess").show();
        $("#pageFail").hide();
    } else {
        $("#pageSuccess").hide();
        $("#pageFail").show();
    }
});