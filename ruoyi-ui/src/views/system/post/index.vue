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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:post:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:post:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:post:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:post:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位编号" align="center" prop="postId" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:post:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:post:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-time"
            @click="handleHistory(scope.row)"
            v-hasPermi="['system:post:query']"
          >历史</el-button>
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

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 岗位历史对话框 -->
    <el-dialog title="岗位变更历史" :visible.sync="historyOpen" width="90%" append-to-body>
      <div class="mb10">
        <el-tag>当前岗位：{{ historyPost.postName }}（{{ historyPost.postCode }}）</el-tag>
      </div>
      <el-table :data="historyList" border style="width: 100%" @selection-change="handleHistorySelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="版本号" align="center" prop="version" width="80" />
        <el-table-column label="操作类型" align="center" prop="operationType" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.operationType === '1'" type="success">新增</el-tag>
            <el-tag v-else-if="scope.row.operationType === '2'" type="warning">修改</el-tag>
            <el-tag v-else-if="scope.row.operationType === '3'" type="danger">删除</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="岗位编码" align="center" prop="postCode" />
        <el-table-column label="岗位名称" align="center" prop="postName" />
        <el-table-column label="岗位排序" align="center" prop="postSort" />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="变更人" align="center" prop="createBy" width="100" />
        <el-table-column label="变更时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变更原因" align="center" prop="changeReason" show-overflow-tooltip />
        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-refresh-left"
              @click="handleRollback(scope.row)"
              v-hasPermi="['system:post:rollback']"
            >回滚</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleViewVersion(scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="mt10 text-center">
        <el-button
          type="primary"
          icon="el-icon-sort"
          size="mini"
          :disabled="historyIds.length !== 2"
          @click="handleCompare"
        >对比选中版本</el-button>
      </div>
    </el-dialog>

    <!-- 版本对比对话框 -->
    <el-dialog title="版本对比" :visible.sync="compareOpen" width="800px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="11">
          <div class="version-header">
            <h4>版本 {{ compareResult.version1 ? compareResult.version1.version : '' }}</h4>
            <p>{{ compareResult.version1 ? parseTime(compareResult.version1.createTime) : '' }}</p>
            <p>操作人：{{ compareResult.version1 ? compareResult.version1.createBy : '' }}</p>
          </div>
        </el-col>
        <el-col :span="2" style="text-align:center;">VS</el-col>
        <el-col :span="11">
          <div class="version-header">
            <h4>版本 {{ compareResult.version2 ? compareResult.version2.version : '' }}</h4>
            <p>{{ compareResult.version2 ? parseTime(compareResult.version2.createTime) : '' }}</p>
            <p>操作人：{{ compareResult.version2 ? compareResult.version2.createBy : '' }}</p>
          </div>
        </el-col>
      </el-row>
      <el-table :data="compareTableData" border style="width: 100%; margin-top: 20px;">
        <el-table-column label="字段" prop="fieldName" width="120" />
        <el-table-column label="旧值" prop="oldValue">
          <template slot-scope="scope">
            <span v-if="scope.row.isChanged" style="color: red; text-decoration: line-through;">{{ scope.row.oldValue }}</span>
            <span v-else>{{ scope.row.oldValue }}</span>
          </template>
        </el-table-column>
        <el-table-column label="新值" prop="newValue">
          <template slot-scope="scope">
            <span v-if="scope.row.isChanged" style="color: green; font-weight: bold;">{{ scope.row.newValue }}</span>
            <span v-else>{{ scope.row.newValue }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 版本查看对话框 -->
    <el-dialog title="版本详情" :visible.sync="viewOpen" width="500px" append-to-body>
      <el-form ref="viewForm" :model="viewForm" label-width="100px">
        <el-form-item label="岗位编码">
          <el-input v-model="viewForm.postCode" readonly />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input v-model="viewForm.postName" readonly />
        </el-form-item>
        <el-form-item label="岗位排序">
          <el-input v-model="viewForm.postSort" readonly />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="viewForm.status" disabled>
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="viewForm.remark" type="textarea" readonly />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { listPost, getPost, delPost, addPost, updatePost, getPostHistoryByPostId, comparePostVersions, rollbackPost } from "@/api/system/post";

export default {
  name: "Post",
  dicts: ['sys_normal_disable'],
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
      // 岗位表格数据
      postList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postCode: undefined,
        postName: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        postName: [
          { required: true, message: "岗位名称不能为空", trigger: "blur" }
        ],
        postCode: [
          { required: true, message: "岗位编码不能为空", trigger: "blur" }
        ],
        postSort: [
          { required: true, message: "岗位顺序不能为空", trigger: "blur" }
        ]
      },
      historyOpen: false,
      compareOpen: false,
      viewOpen: false,
      historyList: [],
      historyPost: {},
      historyIds: [],
      compareResult: {},
      compareTableData: [],
      viewForm: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询岗位列表 */
    getList() {
      this.loading = true;
      listPost(this.queryParams).then(response => {
        this.postList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        postId: undefined,
        postCode: undefined,
        postName: undefined,
        postSort: 0,
        status: "0",
        remark: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.postId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加岗位";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const postId = row.postId || this.ids
      getPost(postId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改岗位";
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.postId != undefined) {
            updatePost(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPost(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const postIds = row.postId || this.ids;
      this.$modal.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？').then(function() {
        return delPost(postIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/post/export', {
        ...this.queryParams
      }, `post_${new Date().getTime()}.xlsx`)
    },
    /** 历史按钮操作 */
    handleHistory(row) {
      this.historyPost = row;
      this.historyOpen = true;
      this.getHistoryList(row.postId);
    },
    /** 查询岗位历史列表 */
    getHistoryList(postId) {
      getPostHistoryByPostId(postId).then(response => {
        this.historyList = response.data;
      });
    },
    /** 历史多选框选中数据 */
    handleHistorySelectionChange(selection) {
      this.historyIds = selection.map(item => item.historyId);
    },
    /** 回滚按钮操作 */
    handleRollback(row) {
      this.$modal.confirm('是否确认回滚到版本「' + row.version + '」？').then(() => {
        return rollbackPost(row.historyId);
      }).then(() => {
        this.getList();
        this.getHistoryList(this.historyPost.postId);
        this.$modal.msgSuccess("回滚成功");
      }).catch(() => {});
    },
    /** 对比按钮操作 */
    handleCompare() {
      if (this.historyIds.length !== 2) {
        this.$modal.msgWarning("请选择两个版本进行对比");
        return;
      }
      comparePostVersions(this.historyIds[0], this.historyIds[1]).then(response => {
        this.compareResult = response.data;
        this.buildCompareTableData();
        this.compareOpen = true;
      });
    },
    /** 构建对比表格数据 */
    buildCompareTableData() {
      const fields = [
        { key: 'postCode', name: '岗位编码' },
        { key: 'postName', name: '岗位名称' },
        { key: 'postSort', name: '岗位排序' },
        { key: 'status', name: '状态' },
        { key: 'remark', name: '备注' }
      ];
      this.compareTableData = [];
      const changes = this.compareResult.changes || {};
      fields.forEach(field => {
        const isChanged = changes[field.key] !== undefined;
        this.compareTableData.push({
          fieldName: field.name,
          oldValue: this.compareResult.version1 ? this.compareResult.version1[field.key] : '',
          newValue: this.compareResult.version2 ? this.compareResult.version2[field.key] : '',
          isChanged: isChanged
        });
      });
    },
    /** 查看版本详情 */
    handleViewVersion(row) {
      this.viewForm = {
        postCode: row.postCode,
        postName: row.postName,
        postSort: row.postSort,
        status: row.status,
        remark: row.remark
      };
      this.viewOpen = true;
    }
  }
};
</script>

<style scoped>
.version-header {
  text-align: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
