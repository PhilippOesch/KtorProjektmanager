var showProfilInfo= document.querySelector(".show-profil-info");
var closeInfo= document.querySelector(".close-info");
var profilInfo= document.querySelector(".profil-info");

showProfilInfo.addEventListener("click", function() {
    profilInfo.style.display= "block";
});

closeInfo.addEventListener("click", function () {
    profilInfo.style.display= "none";
});