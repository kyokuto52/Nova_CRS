/**
 * 
 */

const element = (
	<nav class='navbar navbar-light bg-light fixed-bottom border-top'>
  		<div class='container-fluid d-flex'>
    		<a href='' type="button" class="btn btn-outline-info m-2">活动列表</a>
    		<a href='reservelist' type="button" class="btn btn-outline-info m-2">预约记录</a>
			<a href='mypage' type="button" class="btn btn-outline-info m-2">个人页面</a>
  		</div>
	</nav>
);

ReactDOM.render(element, document.getElementById('root'));