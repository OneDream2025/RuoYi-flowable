<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input
          v-model="queryParams.postCode"
          placeholder="请输入岗位编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input
          v-model="queryParams.postName"
          placeholder="请输入岗位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="变更类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_post_change_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="变更时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-refresh-left"
          size="mini"
          :disabled="single"
          @click="handleRollback"
          v-hasPermi="['system:post:history:rollback']"
        >回滚版本</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-document-copy"
          size="mini"
          :disabled="multiple || selection.length !== 2"
          @click="handleCompare"
          v-hasPermi="['system:post:history:compare']"
        >版本对比</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="historyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="历史记录ID" align="center" prop="historyId" width="100" />
      <el-table-column label="岗位ID" align="center" prop="postId" width="80" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="版本号" align="center" prop="version" width="80">
        <template slot-scope="scope">
          <el-tag type="info">v{{ scope.row.version }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变更类型" align="center" prop="changeType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_post_change_type" :value="scope.row.changeType"/>
        </template>
      </el-table-column>
      <el-table-column label="变更人" align="center" prop="changeBy" width="100" />
      <el-table-column label="变更时间" align="center" prop="changeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.changeTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['system:post:history:query']"
          >查看详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh-left"
            @click="handleRollback(scope.row)"
            v-hasPermi="['system:post:history:rollback']"
          >回滚</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 查看详情对话框 -->
    <el-dialog title="变更详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位编码：">
              <span>{{ form.postCode }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位名称：">
              <span>{{ form.postName }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位排序：">
              <span>{{ form.postSort }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态：">
              <dict-tag :options="dict.type.sys_normal_disable" :value="form.status"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="版本号：">
              <el-tag type="info">v{{ form.version }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="变更类型：">
              <dict-tag :options="dict.type.sys_post_change_type" :value="form.changeType"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="变更人：">
              <span>{{ form.changeBy }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="变更时间：">
              <span>{{ parseTime(form.changeTime) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.remark">
          <el-col :span="24">
            <el-form-item label="备注：">
              <span>{{ form.remark }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <!-- 变更字段对比 -->
      <div v-if="changeDetails && changeDetails.length > 0">
        <el-divider content-position="left">字段变更详情</el-divider>
        <el-table :data="changeDetails" border>
          <el-table-column label="字段名称" align="center" prop="fieldLabel" width="120" />
          <el-table-column label="旧值" align="center" prop="oldValue">
            <template slot-scope="scope">
              <span style="color: #f56c6c; text-decoration: line-through;">{{ scope.row.oldValue || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="新值" align="center" prop="newValue">
            <template slot-scope="scope">
              <span style="color: #67c23a;">{{ scope.row.newValue || '-' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 版本对比对话框 -->
    <el-dialog title="版本对比" :visible.sync="compareOpen" width="900px" append-to-body>
      <el-table :data="compareData.differences" border v-if="compareData.differences && compareData.differences.length > 0">
        <el-table-column label="字段名称" align="center" prop="fieldLabel" width="120" />
        <el-table-column label="版本1 (v{{ compareData.history1 ? compareData.history1.version : '' }})" align="center" prop="oldValue">
          <template slot-scope="scope">
            <span style="color: #f56c6c;">{{ scope.row.oldValue || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="版本2 (v{{ compareData.history2 ? compareData.history2.version : '' }})" align="center" prop="newValue">
          <template slot-scope="scope">
            <span style="color: #67c23a;">{{ scope.row.newValue || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="两个版本内容相同，无差异"></el-empty>
      <div slot="footer" class="dialog-footer">
        <el-button @click="compareOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPostHistory, getPostHistory, getChangeDetail, compareVersions, rollbackVersion } from "@/api/system/postHistory";

export default {
  name: "PostHistory",
  dicts: ['sys_normal_disable', 'sys_post_change_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 岗位变更历史表格数据
      historyList: [],
      // 选中数据
      selection: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postCode: undefined,
        postName: undefined,
        changeType: undefined
      },
      // 详情弹窗
      detailOpen: false,
      // 表单参数
      form: {},
      // 变更详情
      changeDetails: [],
      // 对比弹窗
      compareOpen: false,
      // 对比数据
      compareData: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询岗位变更历史列表 */
    getList() {
      this.loading = true;
      listPostHistory(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.historyList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.detailOpen = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {};
      this.changeDetails = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.selection = selection;
      this.ids = selection.map(item => item.historyId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      this.reset();
      const historyId = row.historyId || this.ids[0];
      getPostHistory(historyId).then(response => {
        this.form = response.data;
        this.changeDetails = response.data.changeDetails || [];
        this.detailOpen = true;
      });
    },
    /** 回滚按钮操作 */
    handleRollback(row) {
      const historyId = row.historyId || this.ids[0];
      const postName = row.postName || this.selection[0].postName;
      const version = row.version || this.selection[0].version;
      this.$modal.confirm('是否确认将岗位"' + postName + '"回滚到版本 v' + version + '？').then(function() {
        return rollbackVersion(historyId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("回滚成功");
      }).catch(() => {});
    },
    /** 版本对比按钮操作 */
    handleCompare() {
      if (this.selection.length !== 2) {
        this.$modal.msgError("请选择两个版本进行对比");
        return;
      }
      const historyId1 = this.selection[0].historyId;
      const historyId2 = this.selection[1].historyId;
      compareVersions({ historyId1, historyId2 }).then(response => {
        this.compareData = response.data;
        this.compareOpen = true;
      });
    }
  }
};
</script>
