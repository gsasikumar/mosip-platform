<html>
<head>
</head>
<body>
<h1>Welcome to ${storeName} Store</h1>

<#assign i = itemList?size>
<p>${i} ${itemName} on Sale!

We are proud to Offer these fine ${itemName} at these amazing prices.

this month only

Choose from :
</p>
<#list itemList as item>
 <p><b>${item.name} for only ${item.price}</b></p>
</#list>
 
 <h4>Call  @ <b>${phoneNo}</b> Today</h4> 
 </body>
</html>