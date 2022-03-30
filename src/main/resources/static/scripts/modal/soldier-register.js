const soldierRegisterForm = document.querySelector("div#soldierRegister form");
soldierRegisterForm.onsubmit = function(event) {
  event.preventDefault();
  alert(JSON.stringify(getSoldier()));
  
  return false;
}

function getSoldier() {
  let soldier = {
    name: soldierRegister.querySelector("input[type=text]#sdName").value,
    militaryOrganizationId: soldierRegister.querySelector("select#sdOm").value,
    militaryRankId: soldierRegister.querySelector("select#sdRank").value,
    phone: soldierRegister.querySelector("input[type=text]#sdPhone").value,
    email: soldierRegister.querySelector("input[type=text]#sdEmail").value
  };
  
  return soldier;
}

