let showProfilInfo= document.querySelector(".show-profil-info");
let closeInfo= document.querySelector(".close-info");
let profilInfo= document.querySelector(".profil-info");

let activateSettingsTab= document.querySelector("#einstellungen-tab-button");
let activateTaskTab= document.querySelector("#task-tab-button");

let settingsTab= document.querySelector("#einstellung-tab");
let taskTab= document.querySelector("#task-tab");

showProfilInfo.addEventListener("click", function() {
    profilInfo.style.display= "block";
});

closeInfo.addEventListener("click", function () {
    profilInfo.style.display= "none";
});

activateSettingsTab.addEventListener("click", function () {
    settingsTab.style.display= "block";
    taskTab.style.display= "none";
    activateSettingsTab.classList.add("active");
    activateTaskTab.classList.remove("active");
});

activateTaskTab.addEventListener("click", function () {
    settingsTab.style.display= "none";
    taskTab.style.display= "block";
    activateSettingsTab.classList.remove("active");
    activateTaskTab.classList.add("active");
});

let taskOverlay= document.querySelector(".overlay-task-container");
let taskOverlayContent= document.querySelector(".overlay-task-content");
let closeTaskOverlay= document.querySelector(".close-task-overlay");
let overlayForm= document.querySelector("#changeTask");

closeTaskOverlay.addEventListener("click", function () {
    taskOverlay.style.display= "none"
})

function openTaskOverlay(evt){
    let infoelement= evt.querySelector(".information");

    let id= infoelement.querySelector("span[name= id]").innerHTML;
    let name= infoelement.querySelector("span[name= name]").innerHTML;
    let description= infoelement.querySelector("span[name= description]").innerHTML;
    let pid= infoelement.querySelector("span[name= pid]").innerHTML;
    let status= infoelement.querySelector("span[name= status]").innerHTML;
    console.log(id);
    console.log(name);
    console.log(description);
    console.log(pid);
    console.log(status);
    overlayForm.action= "/task/"+ id;

    let formOpenTask= taskOverlayContent.querySelector(".start-task");
    let formEndTask= taskOverlayContent.querySelector(".end-task");
    if(status== "OPEN"){
        formOpenTask.style.display= "block";
        formEndTask.style.display= "none";
        formOpenTask.action= "/task/" + id + "/INWORK";
        formOpenTask.querySelector(".project-Id").value= pid;
    } else if(status== "INWORK"){
        formEndTask.style.display= "block";
        formOpenTask.style.display= "none";
        formEndTask.action= "/task/" + id + "/COMPLETED";
        formEndTask.querySelector(".project-Id").value= pid;
    } else {
        formEndTask.style.display= "none";
        formOpenTask.style.display= "none";
    }

    taskOverlayContent.querySelector(".taskname").innerHTML= name;
    taskOverlayContent.querySelector("input[name= pId]").value= pid;
    taskOverlayContent.querySelector("textarea[name= description]").innerHTML= description;

    taskOverlay.style.display= "block";
}

