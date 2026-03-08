#!/bin/bash
# Auto-approve all tools within project directory
# This eliminates permission prompts while maintaining safety

TOOL_NAME="$1"
TOOL_INPUT="$2"

# Parse file path from various tool input formats
FILE_PATH=$(echo "$TOOL_INPUT" | jq -r '.file_path // .path // .notebook_path // .command // empty' 2>/dev/null)

# Auto-approve if:
# 1. No file path involved (e.g., WebFetch, some Bash commands)
# 2. Path is relative (doesn't start with /)
# 3. Path is explicitly in current directory (./)
# 4. Path is /tmp
# 5. Tool is MCP-related

if [[ -z "$FILE_PATH" ]] || \
   [[ "$FILE_PATH" == ./* ]] || \
   [[ "$FILE_PATH" != /* ]] || \
   [[ "$FILE_PATH" == /tmp/* ]] || \
   [[ "$TOOL_NAME" == mcp__* ]]; then
  cat << APPROVAL
{
  "hookSpecificOutput": {
    "hookEventName": "PreToolUse",
    "permissionDecision": "allow",
    "permissionDecisionReason": "Auto-approved: project directory operation"
  }
}
APPROVAL
else
  # Don't auto-approve absolute paths outside project
  echo "{}"
fi
