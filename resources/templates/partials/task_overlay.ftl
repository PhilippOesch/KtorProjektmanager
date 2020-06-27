<div class="overlay-task-container">
    <div class="overlay-task">
        <div class="overlay-task-content">
            <h3><span class="close-task-overlay icon-cross"></span></h3>
            <h1 class="taskname">Test</h1>
            <#--    <p class="taskdescription">Test</p>-->

            <p>
                zugewiesene Nutzer:
            </p>

            <hr>
            <form action="/test" method="post" enctype="application/x-www-form-urlencoded" id="changeTask">
                <div class="form-group">
                    <label>Beschreibung:</label>
                    <textarea form="changeTask" type="text" name="description"></textarea>
                </div>
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Änderungen Speichern"/>
            </form>

            <form class="start-task" action="/test" method="post" enctype="application/x-www-form-urlencoded">
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Aufgabe Beginnen"/>
            </form>

            <form class="end-task" action="/test" method="post" enctype="application/x-www-form-urlencoded">
                <input type="text" class="project-Id" name="pId" value=""/>
                <input type="submit" value="Aufgabe Abschließen"/>
            </form>
        </div>
    </div>
</div>