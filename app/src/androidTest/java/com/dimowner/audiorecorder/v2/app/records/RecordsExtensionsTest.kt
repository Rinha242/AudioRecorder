package com.dimowner.audiorecorder.v2.app.records

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimowner.audiorecorder.v2.data.model.SortOrder
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.MockKAnnotations
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import java.util.Calendar
import kotlin.collections.find

@RunWith(AndroidJUnit4::class)
class RecordsExtensionsTest {

    @MockK
    lateinit var mockContext: Context

    private lateinit var records: List<RecordListItem>
    private lateinit var initialState: RecordsScreenState

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val time = Calendar.getInstance()
        time.set(2025, 5, 20)
        val time1 = time.timeInMillis
        time.set(2025, 5, 22)
        val time2 = time.timeInMillis
        val time3 = time.timeInMillis
        time.set(2024, 7, 20)
        val time4 = time.timeInMillis

        records = listOf(
            RecordListItem(
                recordId = 101,
                name = "Name1",
                details = "Details1",
                duration = "1:01",
                added = time1,
                isBookmarked = false
            ),
            RecordListItem(
                recordId = 202,
                name = "Name2",
                details = "Details2",
                duration = "2:02",
                added = time2,
                isBookmarked = false
            ),
            RecordListItem(
                recordId = 303,
                name = "Name3",
                details = "Details3",
                duration = "3:03",
                added = time3,
                isBookmarked = false
            ),
            RecordListItem(
                recordId = 404,
                name = "Name4",
                details = "Details4",
                duration = "4:04",
                added = time4,
                isBookmarked = false
            ),
        )

        initialState = RecordsScreenState(
            sortOrder = SortOrder.DateDesc,
            recordsMap = records.groupRecordsByDate(mockContext, SortOrder.DateDesc),
        )
    }

    //================ Test mapRecordInMap ====================

    @Test
    fun mapRecordInMap_shouldSuccessfullyUpdateTheTargetRecord() {
        val newName = "New Target Name"
        val recordId = 303L
        val originalMap = records.groupRecordsByDate(
            mockContext, SortOrder.DateDesc
        )

        // The update operation: changing name and bookmark status
        val updateOperation: (RecordListItem) -> RecordListItem = { oldRecord ->
            oldRecord.copy(name = newName, isBookmarked = true)
        }

        // Act
        val newMap = originalMap.mapRecordInMap(recordId, updateOperation)

        // Retrieve the updated record from the new map
        val updatedRecord = newMap["Jun 22"]?.find { it.recordId == recordId }

        // Assert
        assertNotEquals(originalMap, newMap)
        assertEquals(newName, updatedRecord?.name)
        assertTrue(updatedRecord?.isBookmarked == true)
        assertEquals(recordId, updatedRecord?.recordId)
    }

    @Test
    fun mapRecordInMap_shouldReturnLogicallyIdenticalMap_whenIdIsNotFound() {
        val newName = "New Target Name"
        val recordId = 999L
        val originalMap = records.groupRecordsByDate(
            mockContext, SortOrder.DateDesc
        )

        // The update operation: changing name and bookmark status
        val updateOperation: (RecordListItem) -> RecordListItem = { oldRecord ->
            oldRecord.copy(name = newName, isBookmarked = true)
        }

        // Act
        val newMap = originalMap.mapRecordInMap(recordId, updateOperation)

        // Assert
        newMap.values.forEach { list ->
            assertNull(list.find { it.recordId == recordId })
        }

        assertEquals(originalMap, newMap)
        assertNotSame(originalMap, newMap)
    }

    @Test
    fun mapRecordInMap_shouldHandleEmptyMap() {
        // Arrange
        val emptyMap = emptyMap<String, List<RecordListItem>>()
        val updateOperation: (RecordListItem) -> RecordListItem = { it.copy(isBookmarked = true) }

        // Act
        val newMap = emptyMap.mapRecordInMap(101, updateOperation)

        // Assert
        assert(newMap.isEmpty()) { "Processing an empty map should result in an empty map." }
    }

    //=============== Test groupRecordsByDate =====================

    @Test
    fun updateRecordInMap_shouldCreateNewStateAndUpdateRecord() {
        // Arrange
        val newName = "State Update Target"
        val recordId = 303L
        val updateOperation: (RecordListItem) -> RecordListItem = { it.copy(name = newName, isBookmarked = true) }

        // Act
        val newState = initialState.updateRecordInMap(recordId, updateOperation)

        // Retrieve the updated record from the new map
        val updatedRecord = newState.recordsMap["Jun 22"]?.find { it.recordId == recordId }

        // Assert
        assertNotEquals(initialState, newState)
        assertEquals(newName, updatedRecord?.name)
        assertTrue(updatedRecord?.isBookmarked == true)
        assertEquals(recordId, updatedRecord?.recordId)
    }

    @Test
    fun updateRecordInMap_shouldPreserveUnrelatedStateFields() {
        // Arrange
        val newName = "State Update Target"
        val recordId = 303L
        val updateOperation: (RecordListItem) -> RecordListItem = { it.copy(name = newName, isBookmarked = true) }

        // Act
        val newState = initialState.updateRecordInMap(recordId, updateOperation)

        // Assert: Check unrelated state fields are preserved
        assertNotEquals(initialState.recordsMap, newState.recordsMap)
        assertEquals(initialState.selectedRecords, newState.selectedRecords)
        assertEquals(initialState.sortOrder, newState.sortOrder)
        assertEquals(initialState.bookmarksSelected, newState.bookmarksSelected)
        assertEquals(initialState.showDeletedRecordsButton, newState.showDeletedRecordsButton)
        assertEquals(initialState.showRecordPlaybackPanel, newState.showRecordPlaybackPanel)
        assertEquals(initialState.deletedRecordsCount, newState.deletedRecordsCount)
        assertEquals(initialState.isShowProgress, newState.isShowProgress)
        assertEquals(initialState.showRenameDialog, newState.showRenameDialog)
        assertEquals(initialState.showMoveToRecycleDialog, newState.showMoveToRecycleDialog)
        assertEquals(initialState.showMoveToRecycleMultipleDialog, newState.showMoveToRecycleMultipleDialog)
        assertEquals(initialState.showSaveAsDialog, newState.showSaveAsDialog)
        assertEquals(initialState.showSaveAsMultipleDialog, newState.showSaveAsMultipleDialog)
        assertEquals(initialState.operationSelectedRecord, newState.operationSelectedRecord)
        assertEquals(initialState.activeRecord, newState.activeRecord)
    }

    @Test
    fun updateRecordInMap_shouldReturnNewStateButIdenticalRecordContent_whenIdIsNotFound() {
        // Arrange
        val nonExistentId = 999L
        val updateOperation: (RecordListItem) -> RecordListItem = { it.copy(name = "Should Not Be Seen") }

        // Act
        val newState = initialState.updateRecordInMap(nonExistentId, updateOperation)

        // Assert 1: The state object must still be new (due to the outer copy)
        assertNotSame(initialState, newState)
        assertEquals(initialState, newState)
    }

    //=============== Test groupRecordsByDate ======================

    @Test
    fun removeRecordFromMap_shouldRemoveRecordButPreserveGroup() {
        val initialMap = records.groupRecordsByDate(mockContext, SortOrder.DateAsc)
        val recordId = 202L

        // Act
        val newMap = initialMap.removeRecordFromMap(recordId)

        // Assert 1: The key for Group A still exists
        assert(newMap.containsKey("Jun 22")) { "Group key should still exist." }

        // Assert 2: Group A now has only 1 item
        assertEquals(1, newMap["Jun 22"]?.size)

        // Assert 3: The removed record is gone
        val removedRecord = newMap["Jun 22"]?.find { it.recordId == recordId }
        assertNull(removedRecord)
    }

    @Test
    fun removeRecordFromMap_shouldRemoveRecordAndDeleteGroup_whenListBecomesEmpty() {
        val initialMap = records.groupRecordsByDate(mockContext, SortOrder.DateAsc)
        val recordId = 101L

        // Act
        val newMap = initialMap.removeRecordFromMap(recordId)

        // Assert 1: The key for Group (Jun 20) should be gone
        assert(!newMap.containsKey("Jun 20")) { "Group Jun 20 key should be deleted because its list is now empty." }

        // Assert 2: The map size should be reduced from 3 to 2
        assertEquals(2, newMap.size)
    }

    @Test
    fun removeRecordFromMap_shouldReturnLogicallyIdenticalMap_whenIdIsNotFound() {
        // Arrange
        val initialMap = records.groupRecordsByDate(mockContext, SortOrder.DateAsc)
        val nonExistentId = 999L

        // Act
        val newMap = initialMap.removeRecordFromMap(nonExistentId)

        // Assert: The maps are equal but not the same
        assertEquals(3, newMap.size)
        assertEquals(initialMap, newMap)
        assertNotSame(initialMap, newMap)
    }

    @Test
    fun removeRecordFromMap_shouldHandleEmptyMap() {
        // Arrange
        val recordId = 202L
        val emptyMap = emptyMap<String, List<RecordListItem>>()

        // Act
        val newMap = emptyMap.removeRecordFromMap(recordId)

        // Assert
        assert(newMap.isEmpty()) { "Processing an empty map should result in an empty map." }
    }

    //================== Test groupRecordsByDate =========================

    @Test
    fun groupRecordsByDate_shouldGroupItemsByDate_whenSortOrderIsDateAsc() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.DateAsc)
        assertEquals(3, result.size)
        assertEquals(1, result["Jun 20"]?.size)
        assertEquals(2, result["Jun 22"]?.size)
        assertEquals(1, result["Aug 20, 2024"]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldGroupItemsByDate_whenSortOrderIsDateDesc() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.DateDesc)

        assertEquals(3, result.size)
        assertEquals(1, result["Jun 20"]?.size)
        assertEquals(2, result["Jun 22"]?.size)
        assertEquals(1, result["Aug 20, 2024"]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldGroupAllItemsByEmptyString_whenSortOrderIs_NameAsc() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.NameAsc)

        // Assert: Expecting exactly one group with the key ""
        assertEquals(1, result.size)

        // Assert: The single group key should be the empty string
        val singleGroupKey = ""
        assert(result.containsKey(singleGroupKey)) { "The single group key must be an empty string." }
        assertEquals(records.size, result[singleGroupKey]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldGroupAllItemsByEmptyString_whenSortOrderIs_NameDesc() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.NameDesc)

        // Assert: Expecting exactly one group with the key ""
        assertEquals(1, result.size)

        // Assert: The single group key should be the empty string
        val singleGroupKey = ""
        assert(result.containsKey(singleGroupKey)) { "The single group key must be an empty string." }
        assertEquals(records.size, result[singleGroupKey]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldGroupAllItemsByEmptyString_whenSortOrderIs_DurationLongest() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.DurationLongest)

        // Assert: Expecting exactly one group with the key ""
        assertEquals(1, result.size)

        // Assert: The single group key should be the empty string
        val singleGroupKey = ""
        assert(result.containsKey(singleGroupKey)) { "The single group key must be an empty string." }
        assertEquals(records.size, result[singleGroupKey]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldGroupAllItemsByEmptyString_whenSortOrderIs_DurationShortest() {
        val result = records.groupRecordsByDate(mockContext, SortOrder.DurationShortest)

        // Assert: Expecting exactly one group with the key ""
        assertEquals(1, result.size)

        // Assert: The single group key should be the empty string
        val singleGroupKey = ""
        assert(result.containsKey(singleGroupKey)) { "The single group key must be an empty string." }
        assertEquals(records.size, result[singleGroupKey]?.size)
    }

    @Test
    fun groupRecordsByDate_shouldHandleEmptyList() {
        val emptyRecords = emptyList<RecordListItem>()

        val result = emptyRecords.groupRecordsByDate(mockContext, SortOrder.DateAsc)

        assert(result.isEmpty()) { "Grouping an empty list should result in an empty map." }
    }

    @Test
    fun isSortOrderByDate_shouldReturnTrue_forDateAsc() {
        assertTrue(SortOrder.DateAsc.isSortOrderByDate())
        assertTrue(SortOrder.DateDesc.isSortOrderByDate())
        assertFalse(SortOrder.NameAsc.isSortOrderByDate())
        assertFalse(SortOrder.NameDesc.isSortOrderByDate())
        assertFalse(SortOrder.DurationLongest.isSortOrderByDate())
        assertFalse(SortOrder.DurationShortest.isSortOrderByDate())
    }
}
