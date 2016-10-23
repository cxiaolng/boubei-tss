

/* 
 * 数据运算工具方法
 */
;(function($) {

    $.Data = {

        isArray: function(v){
            return Object.prototype.toString.call(v) === '[object Array]';
        },

        /* 
            var a = [
                {'org': '浙江', 'city': '杭州', 'site': '九堡', 'v1': 1, 'v2': '2.2', 'v3': 3}, 
                {'org': '浙江', 'city': '杭州', 'site': '下沙', 'v1': null, 'v2': 2, 'v3': '3.3'}
            ];
            groupby(a, 'org,city', 'v1,v2,v3', 1);
         */
        groupby: function(arr, fields, vFileds, x) {
            vFileds = vFileds || "value";
            var result = [], keySet = [], map = {}, 
                fields = fields.split(","), 
                vFileds = vFileds.split(",");

            arr.each(function(i, row){
                var key = [];
                fields.each(function(i, f){
                    key.push(row[f]);
                });
                key = key.join(",");

                if( !keySet.contains(key) ) {
                    map[key] = {};
                    vFileds.each(function(i, vf){
                        map[key][vf] = 0;
                    });

                    keySet.push(key);
                }

                vFileds.each(function(i, vf){
                    map[key][vf] += parseFloat(row[vf] || '0');
                });
            });

            keySet.each(function(i, key) {
                var item = {}, key = key.split(","), vMap = map[key];
                vFileds.each(function(i, vf){
                    item[vf] = parseFloat( vMap[vf].toFixed( x || 1) );
                });

                fields.each(function(i, f){
                    item[f] = key[i];
                });

                result.push(item);
            });

            return result;
        },
        
        max: function(arr, vField) {
            
        },

        // 计算百分比，保留一位小数
        calculatePercent: function(val1, val2) {
            if( (val1 || val1 == 0) && val2) {
                return parseFloat(val1 * 1000 / val2 / 10).toFixed(1) + "%";
            }
            return '';
        },

        divide: function(val1, val2) {
            if(val1 && val2) {
                return Math.round(val1 * 1000 / val2) / 10;
            }
            return 0;
        },

        combine: function(obj1, obj2) {
            if(obj1 == null) return obj2;
            if(obj2 == null) return obj1;

            var obj3 = {};
            for (var attrname in obj1) { obj3[attrname] = obj1[attrname]; }
            for (var attrname in obj2) { obj3[attrname] = obj2[attrname]; }
            return obj3;
        },

        /* 依据Code列表获取（运单、SKU、车辆）等基本信息，支持超过1000 */
        getByCodes: function(url, _codes, callback) {
            var result = [], i = 0, codes = [];

            if(!_codes || _codes.length == 0) {
                callback([]);
            }

            // 先去重
            _codes.each(function(i, code) {
                !codes.contains(code) && codes.push(code);
            });

            while( i < codes.length ) {
                var sub = codes.slice(i, i=i+500);
                sub.length && tssJS.getJSON( url, {"param1": sub.join(",")},
                    function(data2) {
                        data2.each(function(j, item) {
                            result.push(item);
                        });

                        if(result.length === codes.length) { // 判断是否已全部取到
                            callback(result);
                        }
                    }
                );
            }
        },

        /* 获取采集数据的附件列表信息 */
        getAttachs: function(tableId, itemId, callback) {
            tssJS.ajax({ 
                url: "/tss/auth/xdata/attach/json/" + tableId + "/" + itemId, 
                method: "GET", 
                ondata: function(){
                    var data  = this.getResponseJSON();
                    data && data.each(function(i, item) {
                        callback(item);
                    });
                } 
            });
        },

        /* 屏蔽敏感信息 */
        cover: function(s, from, to) {
            if( !s || !s.length) return s;

            var l = s.length;
            from = from || l - 1;
            to = to || l;
            if( l < from ) return s;

            var a = s.split("");
            for(var i = 0; i < l; i++) {
                if(i >=from-1 && i < to) {
                    a[i] = "*";
                }
            }
            return a.join("");
        },

        /* ---------------------------------- 数据导出 Start ----------------------------------------------- */
        data2CSV: function(name, header, data) {
            if(data && data.length > 0) {
                var params = {"name": name};

                var fields = [], fieldKeys = [];
                header.each(function(i, item){
                    fields.push(item.title);
                    fieldKeys.push(item.field);
                });
                params.data = fields.join(",") + "\n";

                data.each(function(i, row){
                    var values = [];
                    fieldKeys.each(function(i, key){
                        values.push( row[key] );
                    });

                    params.data += values.join(",") + "\n";
                });

                $.Data.exportCSV('/tss/data/export/data2csv', params);
            }
        },

        /*
         * 导出数据为CSV文件。
         * 由数据服务先行生成CSV文件放在服务器的固定目录上，返回文件名称，再以http连接上去下载。
         *
         * 参数1  dataUrl 数据服务地址
         * 参数2  queryParams 数据服务参数
         */
        exportCSV: function(dataUrl, queryParams) {
            /* 创建导出用iframe */
            function createExportFrame() {
                var frameId = "exportFrame";
                if( !$1(frameId) ) {
                    var exportEl = tssJS.createElement("div"); 
                    exportEl.innerHTML = "<div><iframe id='" + frameId + "' src='about:blank' style='display:none'></iframe></div>";
                    document.body.appendChild(exportEl);
                }
                return frameId;
            }

            tssJS.getJSON( dataUrl, queryParams, 
                function(fileName) {  // 根据返回的导出文件名（压缩后的），生成下载链接。
                    if (fileName && fileName.length) {
                        var frameId = createExportFrame();
                        $1(frameId).setAttribute("src", '/tss/data/download/' + fileName[0]);
                    }
                }
            );
        }
        /* ---------------------------------- 数据导出 END ----------------------------------------------- */
    }

})(tssJS);

