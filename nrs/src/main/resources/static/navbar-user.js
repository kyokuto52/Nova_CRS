/**
 * 
 */

const element = (
	<nav class='navbar navbar-light bg-light fixed-bottom border-top'>
  		<div class='container-fluid d-flex'>
    		<div class='flex-grow-1 text-info'>线下活动预约平台 <br/>Reservation System</div>
    		<a href='/reslist' type="button" class="btn btn-outline-info m-2">预约记录</a>
			<a href='/mypage' type="button" class="btn btn-outline-info m-2">个人页面</a>
  		</div>
	</nav>
);

ReactDOM.render(element, document.getElementById('root'));