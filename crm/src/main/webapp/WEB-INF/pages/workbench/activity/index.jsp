<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%= basePath  %>">
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

    <script type="text/javascript">
        $(function () {
            //点击打开创建活动模态窗口
            $("#createActivityBtn").click(function () {
                $("#createActivityForm")[0].reset();//如果需要使用dom对象
                //$("#createActivityForm").get(0).reset();
                $("#createActivityModal").modal("show");
            });

            $("#saveCreateActivityBtn").click(function () {
                var owner = $("#create-marketActivityOwner").val();
                //var owner = document.getElementById("create-marketActivityOwner").value();
                var name = $.trim($("#create-marketActivityName").val());
                var startDate = $("#create-startTime").val();
                var endDate = $("#create-endTime").val();
                var cost = $.trim($("#create-cost").val());
                var description = $.trim($("#create-describe").val());
                //表单验证
                if (owner == "") {
                    alert("所有者不能为空");
                    return;
                }
                if (name == "") {
                    alert("名称不能为空");
                    return;
                }
                if (startDate == "") {
                    alert("开始日期不能为空");
                    return;
                }
                if (endDate == "") {
                    alert("结束日期不能为空");
                    return;
                }
                if (startDate != "" && endDate != "") {
                    if (startDate > endDate) {
                        alert("结束日期不能比开始日期早！");
                        return;
                    }
                }
                var regExp = /^(([1-9]\d*)|0)$/;//非负整数的正则表达式
                if (!regExp.test(cost)) {
                    alert("成本只能是非负整数！");
                    return;
                }
                $.ajax({
                    url: "workbench/activity/saveCreateActivity.do",
                    data: {
                        owner: owner,
                        name: name,
                        startDate: startDate,
                        endDate: endDate,
                        cost: cost,
                        description: description
                    },
                    type: "post",
                    dataType: "json",
                    success: function (data) {
                        if (data.code == "1") {
                            $("#createActivityModal").modal("hide");
                            //$("#page").bs_pagination('getOption', 'rowsPerPage')获取当前显示数量
                            //$("element_id").bs_pagination('getOption','option_name') 第二个参数传想获得什么值，如rowsPerPage,currentPage
                            clearConditionQuery(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message);
                            $("#createActivityModal").modal("show");
                        }
                    }
                })
            });

            //当容器加载完成后，对容器调用工具函数
            $(".mydate").datetimepicker({
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                minView: 'month',
                initialDate: new Date(),
                autoclose: true,
                todayBtn: true,
                clearBtn: true
            });

            queryActivityByConditionForPage(1, 10);

            //给查询按钮添加单击事件
            $("#queryActivityBtn").click(function () {
                queryActivityByConditionForPage(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
            });
            //清空条件查询
            $("#clearConditionBtn").click(function () {
                clearConditionQuery(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
            });
            //给"全选"按钮添加单击事件
            $("#checkAll").click(function () {
                //如果"全选"按钮是选中状态，则所有checkbox都选中
                $("#tbody input[type='checkbox']").prop("checked", this.checked);
            });
            //如果有一个checkbox没有选中，那么全选按钮就不能显示
            $("#tbody").on("click", "input[type='checkbox']", function () {
                if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()) {
                    $("#checkAll").prop("checked", true);
                } else {
                    $("#checkAll").prop("checked", false);
                }
            })

            //删除按钮添加单击事件
            $("#deleteActivityBtn").click(function () {
                //收集参数 获取列表中被选中的checkbox，获得
                var deleteActivities = $("#tbody input[type='checkbox']:checked");
                if (deleteActivities.size() == 0) {
                    alert("需要选中至少一个活动")
                    return;
                }
                if (window.confirm("确定删除？")) {
                    var ids = "";
                    $.each(deleteActivities, function (index, obj) {
                        ids += "id=" + $(obj).val() + "&";
                        //deleteActivities 是jquery对象，obj是循环的dom对象，这里也可以写成 obj.value
                    })
                    ids = ids.substr(0, ids.length - 1);
                    // alert(ids);
                    // alert($("#page").bs_pagination('getOption', 'currentPage'))
                    $.ajax({
                        url: "workbench/activity/deleteActivity.do",
                        data: ids,
                        type: "post",
                        dataType: "json",
                        success: function (data) {
                            if (data.code == "1") {
                                queryActivityByConditionForPage(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                            } else {
                                alert(data.message);
                            }
                        }
                    })
                }
            });

            //修改市场活动单击事件
            $("#editActivityBtn").click(function () {
                var checkedIds = $("#tbody input[type='checkbox']:checked");
                if (checkedIds.size() == 0) {
                    alert("请选择要修改的活动～");
                    return;
                } else if (checkedIds.size() > 1) {
                    alert("只能选择一个市场活动进行修改～");
                    return;
                }
                var id = checkedIds.val();
                $.ajax({
                    url: "workbench/activity/queryActivityById.do",
                    data: {
                        id: id
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        $("#edit-id").val(data.id); //隐藏显示
                        $("#edit-marketActivityOwner").val(data.owner);
                        $("#edit-marketActivityName").val(data.name);
                        $("#edit-startTime").val(data.startDate);
                        $("#edit-endTime").val(data.endDate);
                        $("#edit-cost").val(data.cost);
                        $("#edit-describe").val(data.description);
                        $("#editActivityModal").modal("show");
                    }
                })
            });

            //保存修改的市场活动
            $("#updateActivityBtn").click(function () {
                //收集参数
                var id = $("#edit-id").val();
                var owner = $("#edit-marketActivityOwner").val();
                var name = $.trim($("#edit-marketActivityName").val());
                var startDate = $("#edit-startTime").val();
                var endDate = $("#edit-endTime").val();
                var cost = $.trim($("#edit-cost").val());
                var description = $.trim($("#edit-describe").val());
                //表单验证
                if (owner == "") {
                    alert("所有者不能为空");
                    return;
                }
                if (name == "") {
                    alert("名称不能为空");
                    return;
                }
                if (startDate == "") {
                    alert("开始日期不能为空");
                    return;
                }
                if (endDate == "") {
                    alert("结束日期不能为空");
                    return;
                }
                if (startDate != "" && endDate != "") {
                    if (startDate > endDate) {
                        alert("结束日期不能比开始日期早！");
                        return;
                    }
                }
                var regExp = /^(([1-9]\d*)|0)$/;//非负整数的正则表达式
                if (!regExp.test(cost)) {
                    alert("成本只能是非负整数！");
                    return;
                }
                $.ajax({
                    url: 'workbench/activity/updateActivityById.do',
                    data: {
                        id: id,
                        owner: owner,
                        name: name,
                        startDate: startDate,
                        endDate: endDate,
                        cost: cost,
                        description: description
                    },
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code = "1") {
                            $("#editActivityModal").modal("hide");
                            queryActivityByConditionForPage($("#page").bs_pagination('getOption', 'currentPage'), $("#page").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message);
                            $("#editActivityModal").modal("show");
                        }
                    }
                })
            })

            $("#closeUpdateActivity").click(function () {
                $("#editActivityModal").modal("hide");
            })

            $("#exportActivityAllBtn").click(function () {
                //需要使用同步请求，文件下载
                window.location.href = "workbench/activity/exportAllActivities.do";
            })

            /*
            选择导出
             */
            $("#exportActivityXzBtn").click(function () {
                var selectActivities = $("#tbody input[type='checkbox']:checked");
                if ($(selectActivities).length == 0) {
                    alert("至少选中一个");
                    return;
                }
                var ids = "";
                $.each(selectActivities, function (index, obj) {
                    ids += "id=" + $(obj).val() + "&";
                    //selectActivities 是jquery对象，obj是循环的dom对象，这里也可以写成 obj.value
                })
                ids = ids.substr(0, ids.length - 1);
                window.location.href = "workbench/activity/exportActivitiesByChoose.do?" + ids;
            })

            $("#importActivityBtn").click(function () {
                var activityFileName = $("#activityFile").val();
                var suffix = activityFileName.substr(activityFileName.lastIndexOf(".") + 1).toLocaleLowerCase();
                if (suffix != "xls") {
                    alert("只支持xls文件");
                    return;
                }
                var activityFile = $("#activityFile").get(0).files[0];
                if (activityFile.size > 5 * 1024 * 1024) {
                    alert("文件大小不超过5M");
                    return;
                }
                var formData = new FormData();
                formData.append("activityFile", activityFile);
                $.ajax({
                    url: 'workbench/activity/importActivityByList.do',
                    data: formData,
                    processData: false,
                    contentType: false,
                    type: 'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.code == "1") {
                            alert("成功导入" + data.retData + "条数据");
                            $("#importActivityModal").modal("hide");
                            queryActivityByConditionForPage(1, $("#page").bs_pagination('getOption', 'rowsPerPage'));
                        } else {
                            alert(data.message);
                            $("#importActivityModal").modal("show");
                        }
                    }
                })
            })

            $("#downloadTemplate").click(function () {
                window.location.href = "workbench/activity/downloadTemplate.do";
            })
        });


        function queryActivityByConditionForPage(pageNo, pageSize) {
            //当市场活动主页加载完成，查询所有数据的第一页及数据的总数
            //收集参数
            var name = $("#query-name").val();
            var owner = $("#query-owner").val();
            var startDate = $("#query-startDate").val();
            var endDate = $("#query-endDate").val();
            //发送请求，异步请求，ajax
            $.ajax({
                url: 'workbench/activity/queryActivityByConditionForPage.do',
                data: {
                    name: name,
                    owner: owner,
                    startDate: startDate,
                    endDate: endDate,
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    //显示市场活动的列表
                    //遍历activityList, 拼接行数据
                    var htmlStr = "";
                    $.each(data.activityList, function (index, obj) {
                        htmlStr += " <tr class=\"active\">  ";
                        htmlStr += "  <td><input type=\"checkbox\" value=\"" + obj.id + "\"/></td> ";
                        htmlStr += " <td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id=" + obj.id + "';\">" + obj.name + "</a></td> ";
                        htmlStr += "  <td>" + obj.owner + "</td> ";
                        htmlStr += "  <td>" + obj.startDate + "</td> ";
                        htmlStr += "  <td>" + obj.endDate + "</td> ";
                        htmlStr += "  </tr> ";
                    });
                    $("#tbody").html(htmlStr);
                    $("#checkAll").prop("checked", false);
                    //计算总页数
                    var totalPages = 1;
                    if (data.totalRows % pageSize == 0) {
                        totalPages = data.totalRows / pageSize;
                    } else {
                        totalPages = parseInt(data.totalRows / pageSize) + 1;
                    }
                    //调工具函数bs_pagination,显示翻页信息
                    $("#page").bs_pagination({
                        currentPage: pageNo,
                        rowsPerPage: pageSize,
                        totalRows: data.totalRows,
                        totalPages: totalPages,
                        visiblePageLinks: 7,
                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        onChangePage: function (event, pageObj) {
                            queryActivityByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage);
                        }
                    })
                }
            });
        }

        function clearConditionQuery(pageNo, pageSize) {
            $("#searchFormClear")[0].reset();
            queryActivityByConditionForPage(pageNo, pageSize);
        }
    </script>
</head>
<body>

<!-- 创建市场活动的模态窗口 -->
<div class="modal fade" id="createActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
            </div>
            <div class="modal-body">

                <form id="createActivityForm" class="form-horizontal" role="form">

                    <div class="form-group">
                        <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <%--  与表单元素绑定，获得焦点--%>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="create-marketActivityOwner">
                                <c:forEach items="${users}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-marketActivityName">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control mydate" id="create-startTime" readonly>
                        </div>
                        <label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control mydate" id="create-endTime" readonly>
                        </div>
                    </div>
                    <div class="form-group">

                        <label for="create-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="create-cost" placeholder="万元">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="create-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="create-describe"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="saveCreateActivityBtn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 修改市场活动的模态窗口 -->
<div class="modal fade" id="editActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
            </div>
            <div class="modal-body">

                <form class="form-horizontal" role="form">
                    <input type="hidden" id="edit-id">
                    <div class="form-group">
                        <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <select class="form-control" id="edit-marketActivityOwner">
                                <c:forEach items="${users}" var="u">
                                    <option value="${u.id}">${u.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                style="font-size: 15px; color: red;">*</span></label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control mydate" id="edit-startTime" value="2020-10-10"
                                   readonly>
                        </div>
                        <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control mydate" id="edit-endTime" value="2020-10-20"
                                   readonly>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                        <div class="col-sm-10" style="width: 300px;">
                            <input type="text" class="form-control" id="edit-cost" value="5,000">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10" style="width: 81%;">
                            <textarea class="form-control" rows="3" id="edit-describe">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="closeUpdateActivity">关闭</button>
                <button type="button" class="btn btn-primary" id="updateActivityBtn">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 导入市场活动的模态窗口 -->
<div class="modal fade" id="importActivityModal" role="dialog">
    <div class="modal-dialog" role="document" style="width: 85%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
            </div>
            <div class="modal-body" style="height: 350px;">
                <div style="position: relative;top: 20px; left: 50px;">
                    请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                </div>
                <div style="position: relative;top: 40px; left: 50px;">
                    <input type="file" id="activityFile">
                </div>
                <div style="position: relative;top: 60px; left: 50px;">
                    <input type="button" value="下载模版" id="downloadTemplate">
                </div>
                <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;">
                    <h3>重要提示</h3>
                    <ul>
                        <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                        <li>给定文件的第一行将视为字段名。</li>
                        <li>请确认您的文件大小不超过5MB。</li>
                        <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                        <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                        <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                        <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                    </ul>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
            </div>
        </div>
    </div>
</div>


<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>市场活动列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;" id="searchFormClear">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="query-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="query-owner">
                    </div>
                </div>


                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">开始日期</div>
                        <input class="form-control mydate" type="text" id="query-startDate" readonly/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">结束日期</div>
                        <input class="form-control mydate" type="text" id="query-endDate" readonly>
                    </div>
                </div>

                <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
                <button type="button" class="btn btn-default" id="clearConditionBtn">清空</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" id="createActivityBtn"><span
                        class="glyphicon glyphicon-plus"></span> 创建
                </button>
                <button type="button" class="btn btn-default" id="editActivityBtn"><span
                        class="glyphicon glyphicon-pencil"></span> 修改
                </button>
                <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span
                        class="glyphicon glyphicon-minus"></span> 删除
                </button>
            </div>
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal">
                    <span class="glyphicon glyphicon-export"></span> 上传列表数据（导入）
                </button>
                <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-import"></span> 下载列表数据（批量导出）
                </button>
                <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span
                        class="glyphicon glyphicon-import"></span> 下载列表数据（选择导出）
                </button>
            </div>
        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="checkAll"/></td>
                    <td>名称</td>
                    <td>所有者</td>
                    <td>开始日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="tbody">
                </tbody>
            </table>
            <div id="page">

            </div>
        </div>
    </div>
</div>
</body>
</html>