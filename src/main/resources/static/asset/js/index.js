let form = document.getElementsByClassName("form-sample")[0];
form.addEventListener("submit", (event)=>{
    event.preventDefault();
    console.log("Working");
    payment();
})

let payment = ()=>{
    let inputs = document.getElementsByTagName("input");
    let amount = inputs[0].value;
    let name = inputs[1].value;
    let mobile = inputs[2].value;
    if(amount=="" || amount==null ){
        Swal.fire({ title: "Bad job!", text: "Invalid amount!", icon: "error"});
    }

    $.ajax({
        url:"/submitData",
        data: JSON.stringify({
            amount:amount,
            name:name,
            mobile:mobile
        }),
        contentType: "application/json",
        type:"post",
        dataType:"json",
        success:function(response){
            console.log(response);
            Swal.fire({ title: "Good job!", text: "Your transaction success!", icon: "success"});
        },
        error: function(error){
            console.log(error);
            Swal.fire({ title: "Bad job!", text: "Your transaction failed! "+error, icon: "error"});
        }
    })
}