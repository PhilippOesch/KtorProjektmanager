<div class="task" onclick="openTaskOverlay(this)">
    ${task.task.name}
    <div class="information">
        <span name="id">${task.task.id}</span>
        <span name="name">${task.task.name}</span>
        <span name="description">${task.task.description}</span>
        <span name="pid">${project.id}</span>
        <span name="status">${task.task.status}</span>
        <#if task.users??>
            <ul>
                <#list task.users as user>
                    <li>${user.name}</li>
                </#list>
            </ul>
        </#if>
    </div>
</div>