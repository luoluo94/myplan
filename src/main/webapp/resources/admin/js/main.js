var adminPath="/admin";
var pageSize=20;
var pageNum=1;
var totalPage=1;
var page='?page_size='+pageSize+'&page_num=';

function goto(page){
    $.get("/admin/v?p="+page+'&ran='+Math.random(),function(data){
        $("#right").html(data);
    });
}

function gotoEdit(page){
    $.get("/admin/v?p="+page+'&ran='+Math.random(),function(data){
        $(".list").hide();
        $(".edit").show();
        $(".edit").html(data);
    });
}

function back(){
    $(".edit").hide();
    $(".list").show();
    list();
}
function previous(){
    if(parseInt(pageNum)==1){
        $(".pre").addClass("disabled");
        return;
    }else{
        pageNum=parseInt(pageNum)-1;
        if(pageNum==1){
            $(".pre").addClass("disabled");
        }else{
            $(".ne").removeClass("disabled");
            $(".pre").removeClass("disabled");
        }
    }
    list();
}

function next(){
    if(parseInt(pageNum)==parseInt(totalPage)){
        $(".ne").addClass("disabled");
        return;
    }else{
        pageNum=parseInt(pageNum)+1;
        $(".pre").removeClass("disabled");
        $(".ne").removeClass("disabled");
    }
    list();
}

function search(){
    pageNum=1;
    $(".pre").addClass("disabled");
    $(".ne").removeClass("disabled");
    list();
}


function listMusicTheme(selectName){
    $.ajax({
        url: adminPath+"/video/listMusicTheme",
        type:"POST",
        dataType:"json",
        error:function(){return false},
        success:function(result){
            $("#"+selectName).empty();
            $("#"+selectName).append("<option value=''>未选择</option>");
            $.each(result.data, function (idx, val) {
                $("#"+selectName).append("<option value='" + val["id"] + "'>" + val["name"] + "</option>");
            });
        }
    });

}

function getQueryString(name)
{
    alert(window.location.search);
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}

function setId(id){
    $("#edit_id").val(id);
}

