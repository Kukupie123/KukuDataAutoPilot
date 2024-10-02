package dev.kukukodes.kdap.actionservice.entities

import dev.kukukodes.kdap.actionservice.constants.DbConst
import dev.kukukodes.kdap.actionservice.models.actionNode.ActionNode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(DbConst.CollectionName.UserActionCollection)
data class ActionEntity(
    @Id
    val id: String? = null,
    val name: String,
    val description: String,
    val userId: String,
    val created: LocalDateTime = LocalDateTime.now(),
    val inputStructure: Map<String, Any> = emptyMap(),
    val outputStructure: Map<String, Any> = emptyMap(),
    val actionNode: ActionNode? = null
)