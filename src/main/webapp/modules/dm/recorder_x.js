// ------------------------------------------------- 自定义扩展 ------------------------------------------------
/*
 *  多级下拉选择联动，录入表单和查询表单都使用本方法
 *
 *  参数： nextL    下一级联动参数的序号
        serviceID       下一级联动的service地址             
        currParam       当前联动参数的序号
        currParamValue  当前联动参数的值

    eg: getNextLevelOption(^center^, ^../../api/json/GetFenBo^, ^param1^)  // 数据服务接收参数名为param1
        getNextLevelOption(^center^, ^../../service/centers^, ^org^)      // 数据服务接收参数名为org
 */
function getNextLevelOption(nextL, serviceID, currParam, currParamValue) {
    if( !nextL || !serviceID || !currParam || $.isNullOrEmpty(currParamValue)) return;

    var dreg = /^[1-9]+[0-9]*]*$/, xform;

    nextL = dreg.test(nextL) ? "f" + nextL : nextL;
    if( $("#" + nextL).length == 0 ) return; 

    // serviceID maybe is ID of record, maybe a serviceUrl
    var url = dreg.test(serviceID) ? '../../data/json/' + serviceID : serviceID;
    
    if( currParam.indexOf('p_') >= 0 || url.indexOf('p_') >= 0) { // 查询表单的级联下拉
        currParam = currParam.replace('p_', '')
        url = url.replace('p_', '');
        xform = $.F("searchForm");
    } 
    else {
        xform = $.F("page1Form");
    }

    if( dreg.test(currParam) ) { // 数字
        currParam = "param" + currParam;
    }
    
    $.getNextLevelOption(xform, currParam, currParamValue, url, nextL);
}

/* nextLevel("season", "month", 
 *   {"春":"三月|四月|五月", "夏":"六月|七月|八月", "秋":"九月|十月|十一月", "冬":"十二月|一月|二月"});
 */
function nextLevel(current, next, map) {
    var currentVal = $("#" + current).value();
    var nextOpts = map[currentVal];
    if(!nextOpts) {
        return;
    }

    var xform = $.F("page1Form");
    xform.updateField(next, [
        {"name": "texts", "value": nextOpts},
        {"name": "values", "value": nextOpts}
     ]);
}

function checkBatch(field, expectVal, msg) {
    var values = $.G("grid").getCheckedRowsValue(field);
    var flag = true;
    values.each(function(i, val) {
        if(val != expectVal) {
            flag = false;
        }
    });

    !flag && msg && $.alert(msg);
    return flag;
}

function batchUpdate(field, value) {
    var ids = $.G("grid").getCheckedRows();
    if(!ids) {
        return alert("你没有选中任何记录，请勾选后再进行批量操作。");
    }
    if(ids.length >= 1000) {
        return alert("单次批量操作行数不能超过999行。")
    }
    $.ajax({
        url: URL_BATCH_OPERATE + recordId,
        params: {"ids": ids, "field": field, "value": value},
        onsuccess: function() { 
            loadGridData( $1("GridPageList").value || 1 ); // 更新Grid
        }
    });
}

// addOptBtn('批量及格', function() { batchUpdate("score", "及格") });  
function addOptBtn(name, fn, roles, groups) {
    if( !checkRole(roles) && !checkGroup(groups||'-1212') ) {
        return;
    } 

    var batchOpBtn = $.createElement('button', 'tssbutton small blue');
    $(batchOpBtn).html(name).click( fn );  
    $('#customizeBox').appendChild(batchOpBtn);
}

// batchOpt('批量及格', "score", "及格", "r1,r2", "g1, g2");
function batchOpt(name, field, value, roles, groups) {
    addOptBtn(name, function() { batchUpdate(field, value) }, roles, groups);  
}

function checkRole(roles) {    
    if(!roles) return true; // 默认通过

    var result = false;
    (roles + "").split(",").each(function(i, role){
        if( userRoles.contains( parseInt(role) ) ) {
            result = true;
        }
    });
    return result;
}

function checkGroup(groups) {
    if(!groups) return true; // 默认通过

    var result = false;
    if(userGroups.length) {
        var g = userGroups[userGroups.length - 1];
        (groups + "").split(",").each(function(i, group){
            if(group == (g[0]+"") && group == g[1]) {
                result = true;
            }
        });
    }
    return result;
}

/* 
 * 依据用户的角色和组织判断用户是否能对指定字段可编辑，除指定的角色和组织之外一律不可编辑 
 * forbid( "score", "r1,r2", "g1, g2");
 */
function forbid(field, roles, groups) {
    var editable = false;
    if( (roles && checkRole(roles)) || (groups && checkGroup(groups)) ) {
        editable = true;
    } 

    !editable && permit(field, "false");
}

// check("f1,f2", "User1,User2")
function check(field, users) {
    users = (users || '').split(',');
    !users.contains(userCode) && permit(field, "false");
}

function permit(field, tag) {
    var xform = $.F("page1Form");
    var fields = (field || '').split(",");
    fields.each(function(i, _field) {
        xform.setFieldEditable(_field, tag || "false"); 
    });
}

function calculateSum(totalField, fields) {
    var xform = $.F("page1Form");
    forbid(totalField); 
    fields.each(function(i, field){
        $("#" + field).blur(function(){
            var value = 0;
            fields.each(function(j, f){
                value += getFloatValue(f);
            });
            xform.updateDataExternal(totalField, value);    
            xform.updateData(this);
        });
    });
}

function getFloatValue(field) {
    return parseFloat($("#" + field).value() || '0');
}

// onlyOne(["udf1", "udf2", "udf3"]);
function onlyOne( fields ) {
    var xform = $.F("page1Form"); 
    fields.each(function(i, field){  
        $("#" + field).blur(function(){
            var value = this.value;
            
            fields.each(function(j, f){ 
                if(field !== f) {
                    xform.setFieldEditable(f, !value ? "true" : "false");   
                }
            });

            xform.updateData(this);
        });

        var tempV = $("#" + field).value(); 
        if(tempV) {
            fields.each(function(j, f){
                if(field !== f) {
                    setTimeout(function() {
                        xform.setFieldEditable(f, "false"); 
                    }, 50*j);
                }
            });
        }
    });
}

function notice(field, msg) {
    $("#" + field).click(function(){  
        $(this).notice(msg); 
    });
}

// 隐藏右键的删除按钮
function hideDelButton() {
    $1("grid").contextmenu.delItem("_item_id3");
}

// if($("#udf1").value() === "11") { disableForm(); } else { $("#page1BtSave").show();  };
function disableForm() {
    $("#page1BtSave").hide(); 
    $.F("page1Form").setEditable("false");
}

function updateField(code, value) {
    var xform = $.F("page1Form");
    xform.updateDataExternal(code, value);    
    xform.updateData( $1(code) );
}

function isNew() {
    var tag = ws.getActiveTab().SID;
    if(tag && (tag.indexOf("_new") > 0 || tag.indexOf("_copy") > 0) ) {
        return true;
    }
    return false;
}

function before(day, delta) {
    var today = new Date();
    today.setDate(today.getDate() - delta);
    return new Date(day) < today;
}