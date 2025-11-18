#!/bin/bash

echo "================================================================================"
echo "Maven Central Release Setup Verification"
echo "================================================================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check 1: GPG Key
echo "1. Checking GPG Key..."
if gpg --list-secret-keys --keyid-format=short | grep -q "sec"; then
    KEY_ID=$(gpg --list-secret-keys --keyid-format=short | grep "sec" | awk '{print $2}' | cut -d'/' -f2)
    echo -e "${GREEN}✓${NC} GPG key found: $KEY_ID"
    echo "   Use this value for SIGNING_KEY_ID: ${YELLOW}$KEY_ID${NC}"
else
    echo -e "${RED}✗${NC} No GPG key found"
    exit 1
fi
echo ""

# Check 2: Git Repository
echo "2. Checking Git repository..."
if git remote -v | grep -q "github.com"; then
    REPO_URL=$(git remote get-url origin)
    echo -e "${GREEN}✓${NC} Git repository: $REPO_URL"
else
    echo -e "${RED}✗${NC} No GitHub remote found"
    exit 1
fi
echo ""

# Check 3: Build Configuration
echo "3. Checking build.gradle.kts..."
if grep -q "com.modern.playscraper" build.gradle.kts; then
    GROUP=$(grep "group = " build.gradle.kts | cut -d'"' -f2)
    VERSION=$(grep "version = " build.gradle.kts | cut -d'"' -f2)
    echo -e "${GREEN}✓${NC} Group: $GROUP"
    echo -e "${GREEN}✓${NC} Version: $VERSION"
else
    echo -e "${RED}✗${NC} Invalid build.gradle.kts"
    exit 1
fi
echo ""

# Check 4: GitHub Secrets Required
echo "4. Required GitHub Secrets:"
echo ""
echo "   Go to: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\(.*\)\.git/\1/')/settings/secrets/actions"
echo ""
echo "   Required secrets (2025 Central Portal format):"
echo "   ┌─────────────────────────────┬──────────────────────────────────────┐"
echo "   │ Secret Name                 │ Value                                │"
echo "   ├─────────────────────────────┼──────────────────────────────────────┤"
echo "   │ CENTRAL_PORTAL_USERNAME     │ User Token (starts with 'cntp_')     │"
echo "   │ CENTRAL_PORTAL_PASSWORD     │ User Token Password                  │"
echo "   │ GPG_PRIVATE_KEY             │ base64 encoded GPG private key       │"
echo "   │ GPG_PASSPHRASE              │ Your GPG key passphrase              │"
echo "   │ SIGNING_KEY_ID              │ ${YELLOW}$KEY_ID${NC}                        │"
echo "   │ SIGNING_PASSWORD            │ Same as GPG_PASSPHRASE               │"
echo "   └─────────────────────────────┴──────────────────────────────────────┘"
echo ""
echo "   ${YELLOW}Note:${NC} Generate User Token at https://central.sonatype.com/account"
echo "   (Account → View Account → Generate User Token)"
echo ""

# Check 5: How to export GPG key
echo "5. How to export GPG_PRIVATE_KEY:"
echo ""
echo "   Run this command (will prompt for passphrase):"
echo -e "   ${YELLOW}gpg --armor --export-secret-keys $KEY_ID | base64 | tr -d '\\n' | pbcopy${NC}"
echo ""
echo "   Then paste from clipboard into GitHub Secrets"
echo ""

# Check 6: Ready to release
echo "================================================================================"
echo "Release Checklist:"
echo "================================================================================"
echo ""
echo "  [ ] All 6 GitHub Secrets configured"
echo "  [ ] GPG_PRIVATE_KEY exported and added"
echo "  [ ] SIGNING_KEY_ID set to: $KEY_ID"
echo "  [ ] Code pushed to GitHub"
echo ""
echo "Next steps:"
echo "  1. Configure all GitHub Secrets"
echo "  2. Create release: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\(.*\)\.git/\1/')/releases/new"
echo "     - Tag: v$VERSION"
echo "     - Title: v$VERSION - Initial Release"
echo "     - Copy description from CHANGELOG.md"
echo "  3. Publish release (will trigger automated Maven Central upload)"
echo ""
echo "================================================================================"
