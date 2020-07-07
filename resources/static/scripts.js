let showProfilInfo= document.querySelector(".show-profil-info");
let closeInfo= document.querySelector(".close-info");
let profilInfo= document.querySelector(".profil-info");

let activateSettingsTab= document.querySelector("#einstellungen-tab-button");
let activateTaskTab= document.querySelector("#task-tab-button");
let activateFileTab= document.querySelector("#file-tab-button");

let settingsTab= document.querySelector("#einstellung-tab");
let taskTab= document.querySelector("#task-tab");
let fileTab= document.querySelector("#file-tab");

let tabbuttons= document.querySelectorAll(".tabbutton");
let projectTabs= document.querySelectorAll(".project-tab")

tabbuttons.forEach(value=>{
    value.addEventListener("click", changeProjectTab);
});

showProfilInfo.addEventListener("click", function() {
    profilInfo.style.display= "block";
});

closeInfo.addEventListener("click", function () {
    profilInfo.style.display= "none";
});

function changeProjectTab(_evt){
    let index= _evt.target.getAttribute("value");
    for(let i= 0; i< tabbuttons.length; i++){
        let indexTab= projectTabs.item(i);
        let indexButton= tabbuttons.item(i);
        if(index == i){
            indexTab.style.display= "block";
            indexButton.classList.add("active");
        } else {
            indexTab.style.display= "none";
            indexButton.classList.remove("active");
        }
    }
}


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

    let taskUseremails= [];
    infoelement.querySelectorAll(".task-users li").forEach(value=>{
        taskUseremails.push(value.innerHTML);
    })

    console.log(taskUseremails);
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

    let overlayTaskuserElements= taskOverlayContent.querySelectorAll("input[type= checkbox]");
    overlayTaskuserElements.forEach(value=>{
        value.checked= false
    })

    taskUseremails.forEach((email) =>{
        overlayTaskuserElements.forEach((value)=>{
            if(value.value== email){
                value.checked= true;
            } else {

            }
        })
    })

    taskOverlayContent.querySelector(".taskname").innerHTML= name;
    taskOverlayContent.querySelector("input[name= pId]").value= pid;
    taskOverlayContent.querySelector("textarea[name= description]").innerHTML= description;

    taskOverlay.style.display= "block";
}

