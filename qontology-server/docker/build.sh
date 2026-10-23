# Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
#
# This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
#
# qOntology is licensed under Apache License 2.0 with additional qOntology terms.
# You may use qOntology for commercial purposes, but you may not remove, hide,
# modify, or replace the qOntology logo, copyright notices, license notices,
# or attribution information without a separate commercial license.
#
# White-label use, OEM distribution, rebranding, or presenting qOntology as
# another product requires separate commercial authorization from
# Jiangsu Qiantong Technology Co., Ltd.
#
# Business License: https://community.qontology.ai/business/policy.html
# See the LICENSE file in the project root for full license information.

#!/bin/bash
# 删除旧镜像
# docker rmi qiantong/qontology:1.0.0

# 删除旧实例
# docker buildx rm qontology-builder

# 创建 builder（关键一步）
docker buildx create \
  --name qontology-builder \
  --driver docker-container \
  --use
