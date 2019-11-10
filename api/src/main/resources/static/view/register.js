$().ready(function () {
    var serveUrl = "";
    (function ($) {
        $.getUrlParam = function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return decodeURIComponent(r[2]);
            return null;
        }
    })(jQuery);

    function setRecommend() {
        $("#recommend").val($.getUrlParam("recommend"));
    }

    function openApp() {
        var productId = $.getUrlParam("productId");
        var shopId = $.getUrlParam("shopId");
        if (productId) {
            window.location.href = "gncapp://product?productId=" + productId;
        } else if (shopId) {
            window.location.href = "gncapp://shop?shopId=" + shopId;
        }
    }
    openApp();
    setRecommend();
    getDownloadUrl();
    $.validator.setDefaults({
        submitHandler: function () {
            //alert("提交事件!");
            //form.submit();//提交表单   提交表单到另一个网页的做法


            //用ajax提交表单
            //显示正在加载中....
            //setTimeout('showLoader()', 100);//这里要延迟一下，直接调用无法显示加载器
            //显示加载器.for jQuery Mobile 1.2.0
            function showLoader() {
                $.mobile.loading('show', {
                    text: '正在注册中...', //加载器中显示的文字
                    textVisible: true, //是否显示文字
                    theme: 'a', //加载器主题样式a-e
                    textonly: false, //是否只显示文字
                    html: "" //要显示的html内容，如图片等
                });
            }

            //隐藏加载器.for jQuery Mobile 1.2.0
            function hideLoader() {
                $.mobile.loading('hide');
            }
            showLoader();
            var cellphone = $('#cellphone').val();
            var code = $('#code').val();
            var password = $('#password').val();
            var RPassword = $('#RPassword').val();
            var recommend = $('#recommend').val();
            var citySel = $('#citySel').val();
            var icode = $('#icode').val();
            if (!citySel) {
                return;
            }
            $.ajax({
                type: "post",
                url: serveUrl + "/api/user/register",
                data: {
                    mobile: cellphone,
                    code: code,
                    password: password,
                    repeatPassword: RPassword,
                    recommendCode: recommend,
                    province: first[selectedIndex[0]].text,
                    city: second[selectedIndex[1]].text,
                    area: third[selectedIndex[2]].text,
                    registerSource: 'h5',
                    captcha: icode
                }, //提交到服务器的数据
                dataType: "json", //回调函数接收数据的数据格式
                success: function (res) {
                    if (res.code.toString() == '200') {
                        // hideLoader();//隐藏正在加载中的层
                        // $.mobile.changePage($("#pageSuccess"));
                        // $("#user_reg_success").popup('open');//注册成功后，弹出窗口层
                        location.href = 'download.html'
                    } else {
                        hideLoader(); //隐藏正在加载中的层
                        $('#error_info').html(res.msg);
                        $("#user_reg_err").popup('open'); //注册成功后，弹出窗口层
                    }
                },
                error: function (msg) {
                    hideLoader(); //隐藏正在加载中的层
                }
            });
        }
    });
    // 在键盘按下并释放及提交后验证提交表单
    $("#user_reg").validate({
        rules: {
            cellphone: {
                required: true,
                mobile: true
            },
            password: {
                required: true,
                minlength: 8,
                maxlength: 32,
                checkPwd: true
            },
            RPassword: {
                required: true,
                minlength: 8,
                maxlength: 32,
                checkPwd: true,
                equalTo: "#password" //这里必需填写第一个密码
            },
            code: {
                required: true,
                checkCode: true
            },
            icode: {
                required: true,
                minlength: 4,
                maxlength: 4
            },
            citySel: {
                required: true
            },
            agreement: "required"
        },
        messages: {
            cellphone: {
                required: "请输入手机号",
                //minlength: "用户名必需由{0}个字母组成",
                byteRangeLength: "请输入正确的手机号"
            },
            password: {
                required: "请输入密码",
                minlength: "密码长度不能小于8个字母"
            },
            RPassword: {
                required: "请输入密码",
                minlength: "密码长度不能小于8个字母",
                equalTo: "两次密码输入不一致"
            },
            // code: "请输入验证码",
            code: {
                required: "请输入短信验证码",
            },
            icode: {
                required: "请输入图形验证码",
            },
            citySel: {
                required: "请选择地址",
            },
            agreement: "请先同意用户协议"
        },
        errorPlacement: function (error, element) {
            error.insertAfter(element.parent());
        }
    });

    $.validator.addMethod("mobile", function (value, element) {
        var length = value.length;
        var mobile = /^1[3-9][0-9]{9}/;
        return this.optional(element) || (length == 11 && mobile.test(value));
    }, "请输入正确的手机号");

    $.validator.addMethod("checkCode", function (value, element) {
        var length = value.length;
        var code = /^[0-9]{6}$/;
        return this.optional(element) || (length == 6 && code.test(value));
    }, "请输入正确的短信验证码");

    $.validator.addMethod("checkPwd", function (value, element) {
        var pwd = /^[a-zA-z0-9]{8,32}$/;
        return this.optional(element) || (pwd.test(value));
    }, "*密码是由长度为8-32位的字母和数字组成");

    //获取下载链接
    function getDownloadUrl() {
        $.ajax({
            type: "get",
            url: serveUrl + "/api/version/url",
            dataType: "json", //回调函数接收数据的数据格式
            success: function (res) {
                if (res.code.toString() == '200') {
                    $("#downIos").attr("href", res.data.iosUrl);
                    $("#downAndroid").attr("href", res.data.androidUrl);
                }
            },
            error: function (msg) {
                console.log(msg);
            }
        });
    }
    var disable = false;

    /**
     *获取验证码
     */
    $("#btnCode").click(function () {
        // $.mobile.changePage($("#pageSuccess"));
        // $("#btnCode").text("已发送")
        var cellphone = $('#cellphone').val();
        var icode = $('#icode').val();
        if (!icode) {
            $('#error_info').html('请先输入图形二维码');
            $("#user_reg_err").popup('open');
            return;
        }
        if (!disable && cellphone.match(/^1[3-9][0-9]{9}/) && icode) {
            disable = true;
            $.ajax({
                type: "post",
                url: serveUrl + "/api/sms/code/register",
                data: {
                    mobile: cellphone,
                    captcha: icode
                },
                dataType: "json", //回调函数接收数据的数据格式
                success: function (res) {

                    if (res.code.toString() == '200') {
                        var count = 60;
                        var timer = setInterval(function () {
                            count = count - 1;
                            $("#btnCode").text("已发送" + count + "s");
                            if (count == 0) {
                                disable = false;
                                $("#btnCode").text("重新获取");
                                clearInterval(timer);
                            }
                        }, 1000);
                    } else {
                        disable = false;
                        $('#error_info').html(res.msg);
                        $("#user_reg_err").popup('open');
                    }
                },
                error: function (res) {
                    disable = false
                }
            });
        }

    });
    $('#icodeImage').click(function () {
        $('#icodeImage').attr('src', serveUrl + '/api/captcha.jpg?' + Math.random())
    });
    var nameEl = $("#citySel");

    var first = []; /* 省，直辖市 */
    var second = []; /* 市 */
    var third = []; /* 镇 */

    var selectedIndex = [0, 0, 0]; /* 默认选中的地区 */

    var checked = [0, 0, 0]; /* 已选选项 */

    var city = [];

    function getCity() {
        $.ajax({
            type: "get",
            url: serveUrl + "/api/area/list/all",
            dataType: "json", //回调函数接收数据的数据格式
            success: function (res) {
                if (res.code.toString() == '200') {
                    res.data.areaList.forEach(function (item, index) {
                        var info = {
                            id: item.area.id,
                            name: item.area.name,
                            sub: [],
                        }
                        item.areaChild.forEach(function (sc) {
                            var c = {
                                id: sc.area.id,
                                name: sc.area.name,
                                parent: info.id,
                                sub: []
                            };
                            sc.areaChild.forEach(function (sd) {
                                c.sub.push({
                                    name: sd.area.name,
                                    parent: c.id
                                });
                            });
                            info.sub.push(c);
                        })
                        city.push(info)
                    });
                    creatList(city, first);
                    if (city[selectedIndex[0]].hasOwnProperty('sub')) {
                        creatList(city[selectedIndex[0]].sub, second);
                    } else {
                        second = [{
                            text: '',
                            value: 0
                        }];
                    }

                    if (city[selectedIndex[0]].sub[selectedIndex[1]].hasOwnProperty('sub')) {
                        creatList(city[selectedIndex[0]].sub[selectedIndex[1]].sub, third);
                    } else {
                        third = [{
                            text: '',
                            value: 0
                        }];
                    }

                    createPicker();
                }
            },
            error: function (res) {
                disable = false
            }
        });
    }
    getCity();

    function creatList(obj, list) {
        obj.forEach(function (item, index, arr) {
            var temp = new Object();
            temp.text = item.name;
            temp.id = item.id;
            temp.parent = item.parent;
            temp.value = index;
            list.push(temp);
        })
    }

    var picker;

    function createPicker() {
        picker = new Picker({
            data: [first, second, third],
            selectedIndex: selectedIndex,
            title: '地址选择'
        });

        picker.on('picker.select', function (selectedVal, selectedIndex) {
            var firstArea = first[selectedIndex[0]];
            var secondArea = second[selectedIndex[1]];
            var thirdArea = third[selectedIndex[2]];

            if (secondArea.parent == firstArea.id && thirdArea.parent == secondArea.id) {
                var text1 = first[selectedIndex[0]].text;
                var text2 = second[selectedIndex[1]].text;
                var text3 = third[selectedIndex[2]].text;
                $("#citySel").val(text1 + text2 + text3);
            } else {
                if (secondArea.parent == firstArea.id) {
                    secondChange(selectedIndex[1]);
                } else {
                    firstChange(selectedIndex[0]);
                }
                $("#citySel").val("");
            }
        });


        function firstChange(selectedIndex) {
            second = [];
            third = [];
            checked[0] = selectedIndex;
            var firstCity = city[selectedIndex];
            if (firstCity.hasOwnProperty('sub')) {
                creatList(firstCity.sub, second);

                var secondCity = city[selectedIndex].sub[0]
                if (secondCity.hasOwnProperty('sub')) {
                    creatList(secondCity.sub, third);
                } else {
                    third = [{
                        text: '',
                        value: 0
                    }];
                    checked[2] = 0;
                }
            } else {
                second = [{
                    text: '',
                    value: 0
                }];
                third = [{
                    text: '',
                    value: 0
                }];
                checked[1] = 0;
                checked[2] = 0;
            }

            picker.refillColumn(1, second);
            picker.refillColumn(2, third);
            picker.scrollColumn(1, 0)
            picker.scrollColumn(2, 0)
        }

        function secondChange(selectedIndex) {
            third = [];
            checked[1] = selectedIndex;
            var first_index = checked[0];
            if (city[first_index].sub[selectedIndex].hasOwnProperty('sub')) {
                var secondCity = city[first_index].sub[selectedIndex];
                creatList(secondCity.sub, third);
                picker.refillColumn(2, third);
                picker.scrollColumn(2, 0)
            } else {
                third = [{
                    text: '',
                    value: 0
                }];
                checked[2] = 0;
                picker.refillColumn(2, third);
                picker.scrollColumn(2, 0)
            }
        }

        picker.on('picker.change', function (index, selectedIndex) {
            if (index === 0) {
                firstChange(selectedIndex);
            } else if (index === 1) {
                secondChange(selectedIndex);
            }

        });

        // picker.on('picker.valuechange', function (selectedVal, selectedIndex) {
        //   console.log(selectedVal);
        //   console.log(selectedIndex);
        // });
    }


    $("#address").click(function () {
        $("#citySel").blur();
        if (picker) {
            picker.show();
        }
    });

});