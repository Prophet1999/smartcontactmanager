console.log("Application logged in...");

const toggleSideBar=()=>
{
	if($(".sidebar").is(":visible"))
	{
		//close sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		$(".fa-bars").css("display","inline-block");

	}
	else{
		//open sidebar
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","18%");
		$(".fa-bars").css("display","none");
		
	}
}

const search=()=>{
	//console.log("search done.");
	let query=$("#search-input").val();
	let url=`http://localhost:8080/smartcontacts/user/search/${query}`;
	if(query=="")
	{
		$(".search-result").hide();
	}
	else{
		fetch(url).then((contacts)=>{
			return contacts.json();
		}).then((data)=>{
			let text=`<div class="list-group">`;
			data.forEach(contact => {
				text+=`<a href="/smartcontacts/user/contact/${contact.cid}" class="list-group-item list-group-item-check">${contact.name}</a>`
			});
			text+=`</div>`;
			$(".search-result").html(text);
		})
		
		$(".search-result").show();
	}
}