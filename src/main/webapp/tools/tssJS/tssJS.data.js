

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
        }
    }

})(tssJS);